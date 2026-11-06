const fs = require('fs');
const file = 'app/src/main/java/com/shishir/aiubportal/portal/PortalController.kt';
let t = fs.readFileSync(file, 'utf8');

const oldInject = `    /** Injected at page start: settings for the shim, then the shim itself. */
    private fun injectCore(view: WebView) {
        val s = currentSettings
        val androidSettings = JSONObject().apply {
            put("theme", s.theme)
            put("captchaEnabled", s.captchaEnabled)
            put("extensionEnabled", true)
        }
        view.evaluateJavascript("window.ANDROID_SETTINGS = \${androidSettings};", null)
        view.evaluateJavascript(
            "if (window.APXShim && APXShim.setAndroidSettings) APXShim.setAndroidSettings(\${androidSettings});", null
        )
        view.evaluateJavascript(
            "if (window.APXShim && APXShim.injectForPath) APXShim.injectForPath(location.pathname);", null
        )
    }`;

const newInject = `    /**
     * Injected at page start: the app settings, then the shim script tag.
     * The shim reads window.ANDROID_SETTINGS when it loads, injects the
     * extension CSS + page bundles, and re-runs on page finished if needed.
     */
    private fun injectCore(view: WebView) {
        val s = currentSettings
        val androidSettings = JSONObject().apply {
            put("theme", s.theme)
            put("captchaEnabled", s.captchaEnabled)
            put("extensionEnabled", true)
        }
        view.evaluateJavascript("window.ANDROID_SETTINGS = \${androidSettings};", null)
        view.evaluateJavascript(
            "(function(){" +
                "if (window.__apxInjected) return; window.__apxInjected = true;" +
                "var s = document.createElement('script');" +
                "s.src = 'https://appassets.androidplatform.net/assets/ext/shim.js';" +
                "s.onerror = function(){ console.warn('[AIUB] shim failed to load'); };" +
                "(document.head || document.documentElement).appendChild(s);" +
            "})();", null
        )
    }`;

if (!t.includes(oldInject)) { console.log('injectCore block not found — dumping'); }
else { t = t.replace(oldInject, newInject); console.log('injectCore replaced'); }

t = t.replace(
`            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.startsWith("https://portal.aiub.edu")) {
                    injectCore(view)
                }
            }`,
`            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.startsWith("https://portal.aiub.edu")) {
                    injectCore(view, url)
                }
            }`);

t = t.replace(
`            override fun onPageFinished(view: WebView, url: String) {
                val s = currentSettings
                sessionKeeper.onPageFinished(view, url, s, s.captchaEnabled)
            }`,
`            override fun onPageFinished(view: WebView, url: String) {
                if (url.startsWith("https://portal.aiub.edu")) {
                    /* belt & braces: make sure the shim ran for this page */
                    view.evaluateJavascript(
                        "if (window.APXShim && APXShim.injectForPath) APXShim.injectForPath(location.pathname);", null
                    )
                }
                val s = currentSettings
                sessionKeeper.onPageFinished(view, url, s, s.captchaEnabled)
            }`);

fs.writeFileSync(file, t);
console.log('done. has __apxInjected:', t.includes('__apxInjected'), '| has injectCore(view, url):', t.includes('injectCore(view, url)'));
