/**
 * AIUB Portal App — chrome-API shim for the WebView.
 *
 * Provides the chrome.* surface the AIUB Portal+ Enhanced extension bundles
 * expect (storage.sync/local, runtime.getURL, onChanged), injects the
 * extension's CSS + content-script bundles into the page, and lets the app
 * push settings (theme, captcha toggle) live via APXShim.setAndroidSettings.
 */
(function () {
  if (window.chrome && window.chrome.__apxShim) return;
  if (!window.location.hostname.includes('portal.aiub.edu')) return;

  var EXT_BASE = 'https://appassets.androidplatform.net/assets/ext/';

  /* ---------------- settings storage (localStorage-backed) --------------- */
  var STORE_KEY = 'apxSyncStore';

  function readStore() {
    try { return JSON.parse(localStorage.getItem(STORE_KEY) || '{}'); }
    catch (e) { return {}; }
  }
  function writeStore(obj) {
    try { localStorage.setItem(STORE_KEY, JSON.stringify(obj)); } catch (e) { /* ignore */ }
  }

  var android = window.ANDROID_SETTINGS || {};

  function applyDefaults(result, defaults) {
    var out = {};
    for (var k in defaults) out[k] = defaults[k];
    var stored = readStore();
    for (var k2 in stored) out[k2] = stored[k2];
    /* Android app settings take priority */
    if (android.theme) out.theme = android.theme;
    if (typeof android.captchaEnabled === 'boolean') {
      var ft = out.featureToggles || {};
      ft.captchaSolver = android.captchaEnabled;
      out.featureToggles = ft;
    }
    out.extensionEnabled = android.extensionEnabled !== false;
    return out;
  }

  var changeListeners = [];

  function fireChanged(area, changes) {
    changeListeners.forEach(function (fn) {
      try { fn(changes, area); } catch (e) { /* ignore */ }
    });
  }

  var storageApi = {
    sync: {
      get: function (defaultsOrKeys, cb) {
        var result = {};
        if (Array.isArray(defaultsOrKeys)) {
          defaultsOrKeys.forEach(function (k) { result[k] = readStore()[k]; });
        } else {
          result = applyDefaults(result, defaultsOrKeys || {});
        }
        if (cb) setTimeout(function () { cb(result); }, 0);
      },
      set: function (obj, cb) {
        var store = readStore();
        var changes = {};
        for (var k in obj) {
          changes[k] = { oldValue: store[k], newValue: obj[k] };
          store[k] = obj[k];
        }
        writeStore(store);
        fireChanged('sync', changes);
        if (cb) setTimeout(cb, 0);
      },
      remove: function (keys, cb) {
        var store = readStore();
        (Array.isArray(keys) ? keys : [keys]).forEach(function (k) { delete store[k]; });
        writeStore(store);
        if (cb) setTimeout(cb, 0);
      }
    },
    local: {
      get: function (defaultsOrKeys, cb) {
        var result = {};
        if (Array.isArray(defaultsOrKeys)) {
          defaultsOrKeys.forEach(function (k) { result[k] = readStore()['local_' + k]; });
        } else {
          for (var k in defaultsOrKeys) {
            result[k] = readStore()['local_' + k] !== undefined
              ? readStore()['local_' + k] : defaultsOrKeys[k];
          }
        }
        if (cb) setTimeout(function () { cb(result); }, 0);
      },
      set: function (obj, cb) {
        var store = readStore();
        for (var k in obj) store['local_' + k] = obj[k];
        writeStore(store);
        if (cb) setTimeout(cb, 0);
      }
    },
    onChanged: {
      addListener: function (fn) { changeListeners.push(fn); },
      removeListener: function (fn) {
        var i = changeListeners.indexOf(fn);
        if (i >= 0) changeListeners.splice(i, 1);
      }
    }
  };

  var runtimeApi = {
    getURL: function (p) { return EXT_BASE + String(p).replace(/^\//, ''); },
    sendMessage: function (msg, cb) {
      /* FETCH_NOTICES is handled natively by the app's Notices tab;
         in-page consumers get an empty success payload. */
      if (cb) setTimeout(function () { cb({ success: true, html: '' }); }, 0);
    },
    openOptionsPage: function () {},
    onMessage: {
      addListener: function () {},
      removeListener: function () {}
    }
  };

  window.chrome = window.chrome || {};
  window.chrome.storage = storageApi;
  window.chrome.runtime = runtimeApi;
  window.chrome.__apxShim = true;

  /* ---------------------- Kotlin → JS settings push ---------------------- */
  window.APXShim = {
    setAndroidSettings: function (json) {
      try {
        android = typeof json === 'string' ? JSON.parse(json) : json;
        fireChanged('sync', { android: { newValue: android } });
      } catch (e) { /* ignore */ }
    }
  };

  /* --------------------------- CSS + loaders ---------------------------- */
  function addCss(href) {
    if (document.querySelector('link[href="' + href + '"]')) return;
    var l = document.createElement('link');
    l.rel = 'stylesheet';
    l.href = href;
    (document.head || document.documentElement).appendChild(l);
  }

  function addScript(src) {
    var s = document.createElement('script');
    s.src = src;
    s.async = false;
    (document.head || document.documentElement).appendChild(s);
  }

  var loaders = {
    themeBridge: 'assets/themeBridge.js-C7nNFON7.js',
    captchaSolver: 'assets/CaptchaSolver.js-loader-CoacijFS.js',
    sidebar: 'assets/Sidebar.jsx-CRNG90vf.js',
    navbar: 'assets/Navbar.jsx-loader-D64jxxmQ.js',
    intro: 'assets/Intro.jsx-loader-Dg6XSTBy.js',
    classSchedule: 'assets/ClassSchedule.jsx-loader-DXlnvD7u.js',
    homeRegistration: 'assets/HomeRegistration.jsx-loader-DeeJXYIY.js',
    courseAndResults: 'assets/CourseAndResults.jsx-loader-CJrzsaM2.js',
    academicRegistration: 'assets/AcademicRegistration.jsx-loader-CpFudiPD.js',
    mkCurriculumn: 'assets/MkCurriculumn.jsx-loader-D5RzuNnv.js',
    financials: 'assets/Financials.jsx-loader-BPp6N-2h.js',
    dropApplication: 'assets/DropApplication.jsx-loader-DxuA_buX.js',
    examRoutine: 'assets/ExamRoutine.jsx-loader-DVyh_PaW.js',
    byCurriculum: 'assets/by_carriculum.jsx-loader-C4ggoNTM.js',
    bySemester: 'assets/by_semester.jsx-loader-p6I2lDaO.js',
    changePassword: 'assets/ChangePassword.jsx-loader-_ppjuh_J.js',
    profileContent: 'assets/ProfileContent.jsx-tC5Nr0zm.js',
    onlinePaymentHistory: 'assets/OnlinePaymentHistory.jsx-loader-wmLI6wyB.js',
    offeredFilters: 'assets/OfferedFilters.jsx-loader-D-fd_lvA.js'
  };

  var injected = false;

  function injectForPath(path) {
    if (injected) return;
    injected = true;

    addCss(EXT_BASE + 'src/theme.css');
    addCss(EXT_BASE + 'assets/content-ozNVyx55.css');

    var p = path || location.pathname;

    /* login page: theme + captcha solver */
    if (p === '/' || p.indexOf('/Login') === 0 || p.indexOf('/Account/Login') === 0) {
      addScript(EXT_BASE + loaders.themeBridge);
      addScript(EXT_BASE + loaders.captchaSolver);
      return;
    }

    /* shared UI on every student page */
    if (p.indexOf('/Student') === 0) {
      addScript(EXT_BASE + loaders.sidebar);
      addScript(EXT_BASE + loaders.navbar);

      if (/^\/Student(\/)?$/.test(p) || p.indexOf('/Student/Home') === 0) {
        addScript(EXT_BASE + loaders.intro);
        addScript(EXT_BASE + loaders.classSchedule);
        addScript(EXT_BASE + loaders.homeRegistration);
      } else if (p.indexOf('/Student/Course') === 0) {
        addScript(EXT_BASE + loaders.courseAndResults);
      } else if (p === '/Student/Registration' || p.indexOf('/Student/Registration?') === 0) {
        addScript(EXT_BASE + loaders.academicRegistration);
      } else if (p.indexOf('/Student/Curriculum') === 0) {
        addScript(EXT_BASE + loaders.mkCurriculumn);
      } else if (p.indexOf('/Student/Accounts') === 0) {
        addScript(EXT_BASE + loaders.financials);
      } else if (p.indexOf('/Student/Adrop/DropApplication') === 0) {
        addScript(EXT_BASE + loaders.dropApplication);
      } else if (p.indexOf('/Student/ExamRoutineSchedule') === 0) {
        addScript(EXT_BASE + loaders.examRoutine);
      } else if (p.indexOf('/Student/GradeReport/ByCurriculum') === 0) {
        addScript(EXT_BASE + loaders.byCurriculum);
      } else if (p.indexOf('/Student/GradeReport/BySemester') === 0) {
        addScript(EXT_BASE + loaders.bySemester);
      } else if (p.indexOf('/Student/Credential/ChangePassword') === 0) {
        addScript(EXT_BASE + loaders.changePassword);
      } else if (p.indexOf('/Student/Home/Profile') === 0) {
        addScript(EXT_BASE + loaders.profileContent);
      } else if (p.indexOf('/Student/Payment/List') === 0) {
        addScript(EXT_BASE + loaders.onlinePaymentHistory);
      } else if (p.indexOf('/Student/Section/Offered') === 0) {
        addScript(EXT_BASE + loaders.offeredFilters);
      }
    }
  }

  window.APXShim.injectForPath = injectForPath;

  /* Inject immediately — the app calls us at document-start equivalents. */
  injectForPath(location.pathname);
})();
