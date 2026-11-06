(function(){(function(){if(window.__aiubSidebarEnhanced)return;window.__aiubSidebarEnhanced=!0;function e(e){return e.toLowerCase().replace(/\b\w/g,e=>e.toUpperCase())}function t(){let t=document.querySelector(`.navbar-text .navbar-link small`)||document.querySelector(`.navbar-text .navbar-link`);if(!t)return``;let n=t.textContent.trim(),r=n.split(`,`).map(e=>e.trim());return r.length>=2?e(r[1])+` `+e(r[0]):e(n)}function n(){let e=document.getElementById(`aiub-sidebar-style`);e&&e.remove();let t=document.getElementById(`navigation-bar`);t&&(t.querySelectorAll(`.nav-profile-block, .nav-section-divider, .aiub-graph-nav-item`).forEach(e=>e.remove()),t.querySelectorAll(`.aiub-active-item`).forEach(e=>{e.classList.remove(`active`,`aiub-active-item`)}),t.querySelectorAll(`.aiub-open-collapse`).forEach(e=>{e.classList.remove(`in`,`aiub-open-collapse`)}),t.querySelectorAll(`.aiub-expanded-trigger`).forEach(e=>{e.classList.remove(`aria-expanded-true`,`aiub-expanded-trigger`),e.setAttribute(`aria-expanded`,`false`)}))}function r(){let e=document.getElementById(`navigation-bar`);if(!e)return;let n=t(),r=document.querySelector(`.navbar-text .navbar-link small`),i=(r?r.textContent.trim():``).match(/(\d{2}-\d{5}-\d+)/),a=i?i[1]:``;e.style.cssText=`padding-right: 12px !important; padding-left: 0 !important; padding-top: 4px !important;`;let o=e.querySelector(`.panel-group`);if(o&&(o.style.cssText=`
        margin-bottom: 0 !important;
        border-radius: 12px !important;
        overflow: hidden !important;
        border: 1px solid var(--apx-ov-10) !important;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06) !important;
        background: linear-gradient(180deg, var(--apx-c-f8fafc) 0%, var(--apx-c-e0e7ff) 100%) !important;
      `,o.querySelectorAll(`.panel`).forEach((e,t)=>{e.style.cssText=`
          border: none !important;
          border-radius: 0 !important;
          box-shadow: none !important;
          margin-bottom: 0 !important;
          background: transparent !important;
          border-bottom: ${t===o.children.length-1?`none`:`1px solid rgba(0, 0, 0, 0.05)`} !important;
        `;let n=e.querySelector(`.panel-heading`);if(n){n.style.cssText=`background: transparent !important; border: none !important; padding: 0 !important;`;let e=n.querySelector(`.panel-title`);e&&(e.style.fontSize=`13px !important`);let t=n.querySelector(`a`);if(t){t.style.cssText=`
              display: flex !important;
              align-items: center !important;
              gap: 10px !important;
              padding: 12px 14px !important;
              font-size: 12.5px !important;
              font-weight: 600 !important;
              color: var(--apx-t-4b5563) !important;
              text-decoration: none !important;
              border-radius: 0 !important;
              transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1) !important;
              background: transparent !important;
              position: relative !important;
            `,t.addEventListener(`mouseenter`,function(){this.style.background=`rgba(59, 130, 246, 0.1) !important`,this.style.color=`var(--apx-t-1d4ed8) !important`;let e=this.querySelector(`.fa, .glyphicon`);e&&(e.style.color=`var(--apx-t-2563eb) !important`)}),t.addEventListener(`mouseleave`,function(){let e=this.getAttribute(`aria-expanded`)===`true`;this.style.background=e?`rgba(37, 99, 235, 0.15) !important`:`transparent !important`,this.style.color=e?`var(--apx-t-1e3a8a) !important`:`var(--apx-t-4b5563) !important`;let t=this.querySelector(`.fa, .glyphicon`);t&&(t.style.color=e?`var(--apx-t-2563eb) !important`:`var(--apx-t-9ca3af) !important`)});let e=t.querySelector(`.fa, .glyphicon`);e&&(e.style.cssText=`
                font-size: 12px !important;
                color: var(--apx-t-9ca3af) !important;
                width: 16px !important;
                text-align: center !important;
                flex-shrink: 0 !important;
                transition: color 0.2s ease !important;
              `);let n=t.querySelector(`.fa-caret-down`);n&&(n.style.cssText=`
                margin-left: auto !important;
                font-size: 10px !important;
                color: var(--apx-t-d1d5db) !important;
                transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
                width: auto !important;
                flex-shrink: 0 !important;
              `);let r=()=>{if(t.getAttribute(`aria-expanded`)===`true`){t.style.background=`rgba(37, 99, 235, 0.15) !important`,t.style.color=`var(--apx-t-1e3a8a) !important`,t.classList.add(`aria-expanded-true`,`aiub-expanded-trigger`);let e=t.querySelector(`.fa, .glyphicon`);e&&(e.style.color=`var(--apx-t-2563eb) !important`);let n=t.querySelector(`.fa-caret-down`);n&&(n.style.color=`var(--apx-t-2563eb) !important`,n.style.transform=`rotate(180deg) !important`)}else{t.style.background=`transparent !important`,t.style.color=`var(--apx-t-4b5563) !important`,t.classList.remove(`aria-expanded-true`,`aiub-expanded-trigger`);let e=t.querySelector(`.fa, .glyphicon`);e&&(e.style.color=`var(--apx-t-9ca3af) !important`);let n=t.querySelector(`.fa-caret-down`);n&&(n.style.color=`var(--apx-t-d1d5db) !important`,n.style.transform=`rotate(0deg) !important`)}};r(),t.addEventListener(`click`,()=>setTimeout(r,50))}}let r=e.querySelector(`.panel-collapse`);if(r){r.style.cssText=`border: none !important; background: transparent !important;`;let e=r.querySelector(`div`);e&&(e.style.cssText=`padding: 4px 8px 8px 8px !important;`)}})),e.querySelectorAll(`.list-group-item`).forEach(e=>{e.style.cssText=`
        border: none !important;
        border-radius: 6px !important;
        background: transparent !important;
        padding: 9px 12px !important;
        font-size: 12px !important;
        font-weight: 500 !important;
        color: var(--apx-t-6b7280) !important;
        display: flex !important;
        align-items: center !important;
        gap: 8px !important;
        transition: all 0.15s cubic-bezier(0.4, 0, 0.2, 1) !important;
        text-decoration: none !important;
        line-height: 1.3 !important;
        margin: 0 !important;
        position: relative !important;
      `,e.addEventListener(`mouseenter`,function(){if(!this.classList.contains(`active`)){this.style.color=`var(--apx-t-1e3a8a) !important`;let e=this.querySelector(`.glyphicon`);e&&(e.style.color=`var(--apx-t-2563eb) !important`)}}),e.addEventListener(`mouseleave`,function(){if(!this.classList.contains(`active`)){this.style.color=`var(--apx-t-6b7280) !important`;let e=this.querySelector(`.glyphicon`);e&&(e.style.color=`var(--apx-t-d1d5db) !important`)}});let t=e.querySelector(`.glyphicon`);t&&(t.style.cssText=`
          font-size: 11px !important;
          color: var(--apx-t-d1d5db) !important;
          flex-shrink: 0 !important;
          width: 14px !important;
          text-align: center !important;
          transition: color 0.15s ease !important;
        `)}),n&&!e.querySelector(`.nav-profile-block`)){let t=document.createElement(`div`);t.className=`nav-profile-block`,t.style.cssText=`
        background: linear-gradient(135deg, var(--apx-c-1e3a8a) 0%, var(--apx-c-2563eb) 100%) !important;
        border-radius: 10px !important;
        padding: 16px !important;
        margin-bottom: 12px !important;
        color: var(--apx-t-ffffff) !important;
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25) !important;
        position: relative !important;
        overflow: hidden !important;
        display: flex !important;
        align-items: center !important;
        gap: 10px !important;
      `;let r=document.createElement(`img`);r.src=chrome.runtime.getURL(`logo/icon48.png`),r.alt=`AIUB`,r.style.cssText=`
        width: 36px !important;
        height: 36px !important;
        border-radius: 50% !important;
        border: 2px solid var(--apx-ov-35) !important;
        object-fit: contain !important;
        background: var(--apx-ov-12) !important;
        flex-shrink: 0 !important;
      `,t.appendChild(r);let i=document.createElement(`div`);i.style.cssText=`min-width: 0 !important; flex: 1 !important;`;let o=document.createElement(`div`);if(o.className=`nav-profile-name`,o.style.cssText=`
        font-size: 13px !important;
        font-weight: 700 !important;
        color: var(--apx-t-ffffff) !important;
        margin: 0 0 4px 0 !important;
        white-space: nowrap !important;
        overflow: hidden !important;
        text-overflow: ellipsis !important;
        line-height: 1.3 !important;
        position: relative !important;
        z-index: 1 !important;
        letter-spacing: -0.3px !important;
      `,o.textContent=n,i.appendChild(o),a){let e=document.createElement(`div`);e.className=`nav-profile-id`,e.style.cssText=`
          font-size: 11px !important;
          color: var(--apx-ov-75) !important;
          font-weight: 500 !important;
          letter-spacing: 0.4px !important;
          position: relative !important;
          z-index: 1 !important;
        `,e.textContent=a,i.appendChild(e)}t.appendChild(i),e.insertBefore(t,e.firstChild)}let s=window.location.pathname;e.querySelectorAll(`.list-group-item`).forEach(e=>{try{let t=new URL(e.href).pathname;if(s.startsWith(t)&&t!==`/Student`){e.classList.add(`active`,`aiub-active-item`),e.style.background=`linear-gradient(135deg, var(--apx-c-2563eb) 0%, var(--apx-c-1d4ed8) 100%) !important`,e.style.color=`var(--apx-t-ffffff) !important`,e.style.fontWeight=`600 !important`,e.style.outline=`none !important`,e.style.boxShadow=`0 2px 8px rgba(37, 99, 235, 0.3) !important`;let t=e.querySelector(`.glyphicon`);t&&(t.style.color=`var(--apx-ov-85) !important`)}}catch{}}),e.querySelectorAll(`.panel-group > .panel + .panel`).forEach(e=>{let t=e.previousElementSibling;if(!t||!t.classList||!t.classList.contains(`nav-section-divider`)){let t=document.createElement(`div`);t.className=`nav-section-divider`,t.style.cssText=`height: 6px !important; background: transparent !important; margin: 6px 0 !important;`,e.parentNode.insertBefore(t,e)}})}function i(){document.getElementById(`navigation-bar`)?r():setTimeout(i,150)}function a(e){if(!e){n();return}document.readyState===`loading`?document.addEventListener(`DOMContentLoaded`,i,{once:!0}):i()}chrome.storage.sync.get({extensionEnabled:!0,featureToggles:{}},e=>{a(!!(e&&e.extensionEnabled&&e.featureToggles?.generalUI!==!1))}),chrome.storage.onChanged.addListener((e,t)=>{t!==`sync`||!e.extensionEnabled||(a(!!e.extensionEnabled.newValue),e.extensionEnabled.newValue||n())})})();})()
