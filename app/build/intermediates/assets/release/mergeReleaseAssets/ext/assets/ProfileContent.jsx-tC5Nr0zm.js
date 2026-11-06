(function(){(function(){if(window.__aiubProfileEnhanced)return;window.__aiubProfileEnhanced=!0;function e(){let e=document.querySelector(`#main-content`);if(e){let t=e.querySelector(`.aiub-profile-enhanced`);t&&t.remove()}}function t(){let e=document.querySelector(`#main-content`);if(!e||e.querySelector(`.aiub-profile-enhanced`))return;e.classList.add(`aiub-profile-enhanced`),e.style.cssText=`
      background: linear-gradient(180deg, var(--apx-c-f8fafc) 0%, var(--apx-c-e0e7ff) 100%) !important;
      border-radius: 12px !important;
      padding: 24px !important;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06) !important;
      margin-bottom: 20px !important;
    `;let t=e.querySelector(`.margin5`);t&&(t.style.cssText=`
        background: transparent !important;
        padding: 0 !important;
      `);let n=e.querySelector(`form`);n&&(n.style.cssText=`
        background: transparent !important;
      `);let r=e.querySelector(`fieldset`);if(r){r.style.cssText=`
        border: none !important;
        padding: 0 !important;
        margin: 0 !important;
        background: transparent !important;
      `;let e=r.querySelector(`legend`);e&&(e.style.cssText=`
          font-size: 24px !important;
          font-weight: 800 !important;
          color: var(--apx-t-0f172a) !important;
          margin-bottom: 24px !important;
          padding-bottom: 16px !important;
          border-bottom: 2px solid var(--apx-ov-30) !important;
          font-family: system-ui,-apple-system,sans-serif !important;
          width: 100% !important;
        `)}let i=e.querySelector(`.row`);i&&(i.style.cssText=`
        gap: 20px !important;
      `);let a=e.querySelector(`.col-md-8`);if(a){a.style.cssText=`
        background: var(--apx-ov-70) !important;
        border-radius: 10px !important;
        padding: 20px !important;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06) !important;
      `;let e=a.querySelector(`[style*="margin-bottom"]`);e&&(e.style.cssText=`
          margin-bottom: 20px !important;
          padding-bottom: 16px !important;
          border-bottom: 2px solid var(--apx-c-e2e8f0) !important;
          font-family: system-ui,-apple-system,sans-serif !important;
        `);let t=a.querySelector(`.table`);t&&(t.style.cssText=`
          margin-bottom: 0 !important;
          font-family: system-ui,-apple-system,sans-serif !important;
          border-collapse: collapse !important;
        `,t.querySelectorAll(`tr`).forEach((e,t)=>{e.style.cssText=`
            border-bottom: 1px solid var(--apx-c-e2e8f0) !important;
            background: transparent !important;
          `,t%2==0&&(e.style.background=`transparent !important`),e.querySelectorAll(`td`).forEach((e,t)=>{e.style.cssText=`
              padding: 12px 14px !important;
              font-size: 13px !important;
              color: var(--apx-t-374151) !important;
              font-weight: 500 !important;
              vertical-align: middle !important;
              border: none !important;
            `,t===0&&e.textContent.trim()?e.style.cssText+=`
                color: var(--apx-t-1e3a8a) !important;
                font-weight: 600 !important;
                width: 30% !important;
                text-align: right !important;
                padding-right: 16px !important;
              `:e.style.cssText+=`
                color: var(--apx-t-6b7280) !important;
                text-align: left !important;
              `,e.textContent.trim()===``&&(e.style.height=`8px !important`,e.style.paddingTop=`4px !important`,e.style.paddingBottom=`4px !important`)})}))}let o=e.querySelector(`.col-md-4`);if(o){o.style.cssText=`
        display: flex !important;
        align-items: flex-start !important;
        justify-content: center !important;
      `;let e=o.querySelector(`img`);e&&(e.style.cssText=`
          max-width: 100% !important;
          height: auto !important;
          border-radius: 10px !important;
          box-shadow: 0 4px 12px rgba(30, 58, 138, 0.15) !important;
          border: 2px solid var(--apx-ov-50) !important;
          object-fit: cover !important;
        `)}e.querySelectorAll(`h2`).forEach(e=>{e.style.cssText=`
        font-size: 22px !important;
        font-weight: 800 !important;
        color: var(--apx-t-0f172a) !important;
        margin: 0 0 4px 0 !important;
        line-height: 1.3 !important;
        font-family: system-ui,-apple-system,sans-serif !important;
      `}),e.querySelectorAll(`p`).forEach(e=>{e.style.cssText=`
        font-size: 13px !important;
        color: var(--apx-t-94a3b8) !important;
        margin: 0 !important;
        font-family: system-ui,-apple-system,sans-serif !important;
      `})}function n(){document.querySelector(`#main-content`)?t():setTimeout(n,150)}function r(t){if(!t){e();return}document.readyState===`loading`?document.addEventListener(`DOMContentLoaded`,n,{once:!0}):n()}chrome.storage.sync.get({extensionEnabled:!0,featureToggles:{}},e=>{r(!!(e&&e.extensionEnabled&&e.featureToggles?.profile!==!1))}),chrome.storage.onChanged.addListener((t,n)=>{n!==`sync`||!t.extensionEnabled||(r(!!t.extensionEnabled.newValue),t.extensionEnabled.newValue||e())})})();})()
