import{o as e}from"./chunk-zsgVPwQN.js";import{n as t,t as n}from"./jsx-runtime-C3SrMh-X.js";import{o as r}from"./fi-m_7rYGJu.js";var i=e(t(),1);async function a(){try{let e=await new Promise(e=>{chrome.runtime.sendMessage({type:`FETCH_NOTICES`},t=>{e(t)})});if(!e||!e.success)throw Error(e?e.error:`No response from background script`);let t=e.html,n=[],r=t.split(`class="notification"`);for(let e=1;e<r.length;e++){let t=r[e],i=t.match(/date-custom[^>]*>[\s\S]*?(\d+)[\s\S]*?([A-Za-z]+)(?:[\s\S]*?<span[^>]*>(\d{4})<\/span>)?/i);if(!i)continue;let a=i[1].trim(),o=i[2].trim(),s=i[3]?i[3].trim():new Date().getFullYear(),c=``,l=t.match(/<h2[^>]*title[^>]*>([^<]+)<\/h2>/i);if(l)c=l[1].trim();else{let e=t.match(/notification-text[^>]*>\s*([^<]+)/i);e&&(c=e[1].trim())}c=c.replace(/&amp;/g,`&`).replace(/&lt;/g,`<`).replace(/&gt;/g,`>`).replace(/\s+/g,` `);let u=``,d=t.match(/href="([^"]+)"/i);if(d)u=d[1].trim();else{let t=r[e-1].match(/<a\s+[^>]*href="([^"]+)"[^>]*>\s*$/i);t&&(u=t[1].trim())}u.startsWith(`/`)&&(u=`https://aiub.edu`+u),c&&u&&n.push({title:c,date:`${a} ${o} ${s}`,url:u})}return n}catch(e){return console.error(`Error scraping notices:`,e),[]}}var o=n();(function(){if(window.__aiubNavbarEnhanced)return;chrome.storage.sync.get({extensionEnabled:!0,featureToggles:{}},t=>{if(!t.extensionEnabled||t.featureToggles?.generalUI===!1)return;function n(){let t=document.querySelector(`.topbar-container`);if(!t){setTimeout(n,200);return}window.__aiubNavbarEnhanced||(window.__aiubNavbarEnhanced=!0,e(t))}document.readyState===`loading`?document.addEventListener(`DOMContentLoaded`,n):n()});function e(e){let s=e.closest(`nav`)||e.closest(`.navbar`)||e.parentElement;s&&(s.style.cssText=`
        background: linear-gradient(180deg, var(--apx-c-f8fafc) 0%, var(--apx-c-e0e7ff) 100%) !important;
        border: none !important;
        border-bottom: 2px solid var(--apx-c-1a73c8) !important;
        box-shadow: 0 2px 8px rgba(26,115,200,0.10) !important;
        min-height: 85px !important;
        height: 85px !important;
        padding: 0 !important;
        margin-bottom: 20px !important;
        overflow: visible !important;
        display: flex !important;
        align-items: center !important;
      `),e.style.cssText=`
      background: transparent !important;
      min-height: 85px !important;
      height: 85px !important;
      padding: 0 !important;
      width: 100% !important;
      box-sizing: border-box !important;
      display: flex !important;
      align-items: center !important;
      justify-content: space-between !important;
      overflow: visible !important;
    `;let c=e.querySelector(`.navbar-header`);c&&(c.style.cssText=`
        display: flex !important;
        align-items: center !important;
        float: left !important;
        margin-right: 0 !important;
        overflow: visible !important;
        height: 85px !important;
        min-height: 85px !important;
      `);let l=e.querySelector(`.navbar-brand`);if(l){let e=l.querySelector(`.aiub-logo-text`);e&&e.remove(),l.style.cssText=`
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        padding: 5px 12px !important;
        float: left !important;
        height: 85px !important;
        min-height: 85px !important;
        line-height: 1 !important;
        text-decoration: none !important;
        overflow: visible !important;
        box-sizing: border-box !important;
      `,l.querySelectorAll(`img`).forEach(e=>{e.style.cssText=`
          max-height: 60px !important;
          height: auto !important;
          width: auto !important;
          display: block !important;
          object-fit: contain !important;
        `})}let u=e.querySelector(`.navbar-header`);u&&(u.style.overflow=`visible !important`);let d=e.querySelector(`.navbar-toggle`);d&&(d.style.display=`none !important`);let f=e.querySelector(`.navbar-collapse`);f&&(f.style.cssText=`
        padding: 0 8px 0 0 !important;
        background: transparent !important;
        border: none !important;
        box-shadow: none !important;
        display: flex !important;
        align-items: center !important;
        justify-content: flex-end !important;
        margin-left: auto !important;
        height: 85px !important;
        min-height: 85px !important;
      `);let p=e.querySelector(`ul.nav.navbar-nav.hidden-md`);p&&(p.style.cssText=`
        float: left !important;
        margin: 0 !important;
        padding: 0 !important;
        list-style: none !important;
        height: 85px !important;
      `,p.querySelectorAll(`li`).forEach(e=>{e.style.cssText=`float: left !important; display: flex !important; align-items: center !important; position: relative !important; height: 85px !important;`;let n=e.querySelector(`a`);n&&t(n)}));let m=e.querySelectorAll(`.navbar-collapse .navbar-nav`),h=null;m.forEach(e=>{e.querySelectorAll(`li`).forEach(e=>{e.querySelector(`a[href*="Logout"]`)&&(h=e)})}),m.forEach(e=>{e.style.cssText=`
        margin: 0 !important;
        padding: 0 !important;
        list-style: none !important;
        display: flex !important;
        align-items: center !important;
        height: 85px !important;
        float: none !important;
      `,e.querySelectorAll(`li`).forEach(e=>{e!==h&&(e.style.cssText=`
          display: flex !important;
          align-items: center !important;
          height: 85px !important;
          position: relative !important;
          float: none !important;
        `)})}),h&&(h.style.cssText=`
        display: flex !important;
        align-items: center !important;
        height: 85px !important;
        position: relative !important;
        float: none !important;
        margin-left: auto !important;
      `);let g=e.querySelector(`.navbar-text`);if(g){g.style.cssText=`
        display: flex !important;
        align-items: center !important;
        float: none !important;
        margin: 0 !important;
        padding: 0 10px !important;
        height: 85px !important;
        color: var(--apx-t-374151) !important;
        font-size: 14px !important;
        font-family: system-ui,-apple-system,sans-serif !important;
        font-weight: 500 !important;
        white-space: nowrap !important;
        line-height: 1 !important;
      `;let e=g.querySelector(`.aiub-welcome-label`);e&&e.remove(),Array.from(g.childNodes).forEach(e=>{e.nodeType===Node.TEXT_NODE&&e.textContent.trim()===``&&(e.textContent=`Welcome `)});let t=g.querySelector(`.navbar-link`);if(t){t.style.cssText=`
          color: var(--apx-t-1a73c8) !important;
          font-weight: 700 !important;
          font-size: 14px !important;
          text-decoration: none !important;
          font-family: system-ui,-apple-system,sans-serif !important;
          text-transform: uppercase !important;
          margin-left: 6px !important;
        `;let e=t.querySelector(`small`);e&&(e.style.cssText=`
            font-size: 14px !important;
            font-weight: 700 !important;
            color: var(--apx-t-1a73c8) !important;
            text-transform: uppercase !important;
          `),t.addEventListener(`mouseenter`,()=>{t.style.color=`var(--apx-t-0d47a1) !important`}),t.addEventListener(`mouseleave`,()=>{t.style.color=`var(--apx-t-1a73c8) !important`})}}if(h){let e=h.querySelector(`a`);e&&n(e,`var(--apx-t-e53e3e)`)}let _=e.querySelector(`#noti_Button`);if(_){_.style.cssText=`
        display: flex !important;
        align-items: center !important;
        cursor: pointer !important;
        padding: 0 !important;
        height: 85px !important;
      `,_.addEventListener(`mouseenter`,()=>{_.style.background=`transparent !important`}),_.addEventListener(`mouseleave`,()=>{_.style.background=`transparent !important`});let e=_.querySelector(`div`);e&&n(e,`var(--apx-t-1a73c8)`,null,!0)}let v=e.querySelector(`li.dropdown > a.dropdown-toggle`);v&&n(v,`var(--apx-t-1a73c8)`);let y=e.querySelector(`#noti_Counter`);if(y){let e=`
        position: absolute !important;
        top: 8px !important;
        right: 2px !important;
        min-width: 16px !important;
        height: 16px !important;
        background: var(--apx-c-ef4444) !important;
        color: var(--apx-t-ffffff) !important;
        font-size: 9px !important;
        font-weight: 800 !important;
        border-radius: 999px !important;
        text-align: center !important;
        line-height: 12px !important;
        padding: 0 3px !important;
        z-index: 10 !important;
        border: 2px solid var(--apx-c-ffffff) !important;
        transition: opacity 0.2s !important;
      `,t=()=>{let t=y.textContent.trim();!t||t===`0`?y.style.cssText=e+`opacity: 0 !important; pointer-events: none !important;`:y.style.cssText=e+`opacity: 1 !important; pointer-events: auto !important;`};t(),new MutationObserver(t).observe(y,{childList:!0,characterData:!0,subtree:!0})}let b=e.querySelector(`#notifications`);if(b){b.style.cssText=`
        background: var(--apx-ov-85) !important;
        backdrop-filter: blur(16px) !important;
        -webkit-backdrop-filter: blur(16px) !important;
        border: 1px solid var(--apx-ov-40) !important;
        border-radius: 12px !important;
        box-shadow: 0 8px 32px rgba(0,0,0,0.14) !important;
        padding: 0 !important;
        z-index: 9999 !important;
        min-width: 320px !important;
        top: 85px !important;
      `;let e=b.querySelector(`h3`);if(e){e.style.cssText=`
          margin: 0 !important; padding: 12px 16px !important;
          font-size: 14px !important; font-weight: 700 !important; color: var(--apx-t-1e3a5f) !important;
          background: transparent !important;
          border-bottom: 1px solid rgba(0,0,0,0.05) !important;
          font-family: system-ui,-apple-system,sans-serif !important;
          display: flex !important; justify-content: space-between !important; align-items: center !important;
        `;let t=e.querySelector(`a`);t&&(t.style.cssText=`font-size:11px !important;font-weight:600 !important;color: var(--apx-t-1a73c8) !important;text-decoration: none !important;float:none !important;`)}let t=b.querySelector(`.col-md-12[style*="overflow"]`);t&&(t.style.cssText=`max-height:400px !important;overflow-y:auto !important;padding:8px !important;`);let n=b.querySelector(`.seeAll`);if(n){n.style.cssText=`padding:12px !important;margin:8px 8px 8px 8px !important;text-align:center !important;background: rgba(26,115,200,0.06) !important;border-radius: 8px !important;transition:background 0.2s !important;border-top: none !important;`,n.addEventListener(`mouseenter`,()=>{n.style.background=`rgba(26,115,200,0.12) !important`}),n.addEventListener(`mouseleave`,()=>{n.style.background=`rgba(26,115,200,0.06) !important`});let e=n.querySelector(`a`);e&&(e.style.cssText=`font-size:13px !important;font-weight:700 !important;color: var(--apx-t-1a73c8) !important;text-decoration: none !important;display:block !important;`)}}let x=e.querySelector(`.dropdown-menu`);x&&(x.style.cssText=`
        background: var(--apx-c-ffffff) !important; border: 1px solid var(--apx-c-e2e8f0) !important;
        border-radius: 10px !important; box-shadow: 0 6px 24px rgba(0,0,0,0.12) !important;
        padding: 6px !important; min-width: 180px !important; top: 77px !important;
      `,x.querySelectorAll(`li > a`).forEach(e=>{e.style.cssText=`display:flex !important;align-items:center !important;padding:9px 13px !important;font-size:13px !important;font-weight:500 !important;color: var(--apx-t-374151) !important;border-radius: 8px !important;text-decoration: none !important;font-family:system-ui,-apple-system,sans-serif !important;`,e.addEventListener(`mouseenter`,()=>{e.style.background=`var(--apx-c-f0f7ff) !important`,e.style.color=`var(--apx-t-1a73c8) !important`}),e.addEventListener(`mouseleave`,()=>{e.style.background=`transparent !important`,e.style.color=`var(--apx-t-374151) !important`})}));let S=window.location.pathname;if(e.querySelectorAll(`a[href]`).forEach(e=>{try{let t=e.getAttribute(`href`);if(!t||t===`#`)return;let n=new URL(t,window.location.origin).pathname;S.startsWith(n)&&n!==`/Student`&&(e.style.background=`rgba(26,115,200,0.10) !important`,e.style.color=`var(--apx-t-1a73c8) !important`,e.style.borderRadius=`6px !important`,e.querySelectorAll(`.fa,.glyphicon`).forEach(e=>e.style.color=`var(--apx-t-1a73c8) !important`))}catch{}}),setTimeout(async()=>{try{let t=e.querySelector(`#noti_Button`);if(!t)return;let n=t.parentElement,s=n.parentElement;if(!s)return;let c=document.createElement(`li`);c.style.cssText=`
          display: flex !important;
          align-items: center !important;
          height: 85px !important;
          position: relative !important;
          float: none !important;
          margin-right: 14px !important;
        `,c.innerHTML=`
          <a id="aiub_custom_notices_btn" style="display: inline-flex !important; align-items: center !important; justify-content: center !important; gap: 7px !important; padding: 6px 14px !important; height: 36px !important; border-radius: 9999px !important; background: linear-gradient(135deg, var(--apx-c-eff6ff) 0%, var(--apx-c-dbeafe) 100%) !important; border: 1px solid var(--apx-c-bfdbfe) !important; color: var(--apx-t-1e40af) !important; font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important; text-decoration: none !important; transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1) !important; cursor: pointer !important; box-shadow: 0 1px 3px rgba(37,99,235,0.08) !important; position: relative !important;">
            <span id="aiub_custom_notices_icon_wrapper" style="display: flex; align-items: center; justify-content: center; color: var(--apx-t-2563eb);"></span>
            <span style="font-size: 12px; font-weight: 650; color: var(--apx-t-1e40af); letter-spacing: -0.1px; white-space: nowrap;">AIUB Notices</span>
            <div id="aiub_custom_notices_counter" style="display: none !important; min-width: 18px !important; height: 18px !important; background: var(--apx-c-ef4444) !important; color: var(--apx-t-ffffff) !important; font-size: 10px !important; font-weight: 800 !important; border-radius: 9999px !important; align-items: center !important; justify-content: center !important; padding: 0 4px !important; margin-left: 2px !important; box-shadow: 0 1px 4px rgba(239,68,68,0.3) !important;"></div>
          </a>
          <div id="aiub_custom_notices_dropdown" style="display: none; position: absolute; top: 77px; right: -50px; width: 380px; background: var(--apx-ov-85); backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px); border: 1px solid var(--apx-ov-40); border-radius: 12px; box-shadow: 0 8px 32px rgba(0,0,0,0.14); padding: 0; z-index: 9999; max-height: 400px; overflow-y: auto;">
             <div style="padding: 12px 16px; font-weight: 700; color: var(--apx-t-1e3a5f); border-bottom: 1px solid rgba(0,0,0,0.05); font-family: system-ui, sans-serif; display: flex; justify-content: space-between; align-items: center;">
               Latest Notices from aiub.edu
               <a href="#" id="aiub_custom_notices_close" style="font-size:11px; font-weight:600; color: var(--apx-t-1a73c8); text-decoration: none;">Close</a>
             </div>
             <div id="aiub_custom_notices_list" style="padding: 8px;">
               <div style="padding: 20px; text-align: center; color: var(--apx-t-64748b); font-size: 13px;">Loading notices...</div>
             </div>
          </div>
        `,s.insertBefore(c,n),(0,i.createRoot)(c.querySelector(`#aiub_custom_notices_icon_wrapper`)).render((0,o.jsx)(r,{size:15}));let l=c.querySelector(`#aiub_custom_notices_btn`),u=c.querySelector(`#aiub_custom_notices_dropdown`),d=c.querySelector(`#aiub_custom_notices_list`),f=c.querySelector(`#aiub_custom_notices_counter`),p=c.querySelector(`#aiub_custom_notices_close`);p&&p.addEventListener(`click`,e=>{e.preventDefault(),e.stopPropagation(),u.style.display=`none`}),l.addEventListener(`mouseenter`,()=>{l.style.background=`linear-gradient(135deg, var(--apx-c-dbeafe) 0%, var(--apx-c-bfdbfe) 100%) !important`,l.style.borderColor=`var(--apx-t-93c5fd) !important`,l.style.boxShadow=`0 3px 8px rgba(37,99,235,0.18) !important`}),l.addEventListener(`mouseleave`,()=>{l.style.background=`linear-gradient(135deg, var(--apx-c-eff6ff) 0%, var(--apx-c-dbeafe) 100%) !important`,l.style.borderColor=`var(--apx-t-bfdbfe) !important`,l.style.boxShadow=`0 1px 3px rgba(37,99,235,0.08) !important`}),l.addEventListener(`click`,e=>{e.preventDefault(),e.stopPropagation(),n&&n.classList.contains(`open`)&&n.classList.remove(`open`);let t=u.style.display===`none`;u.style.display=t?`block`:`none`,t&&(f.style.display=`none !important`)}),t.addEventListener(`click`,()=>{u.style.display=`none`}),document.addEventListener(`click`,e=>{c.contains(e.target)||(u.style.display=`none`)});let m=await a();if(m&&m.length>0){let e=m.slice(0,15);f.textContent=e.length.toString(),f.style.display=`flex !important`,d.innerHTML=``,e.forEach((e,t)=>{let n=document.createElement(`a`);n.href=e.url,n.target=`_blank`,n.style.cssText=`
              display: block;
              padding: 10px 12px;
              margin-bottom: 6px;
              text-decoration: none;
              background: ${t%2==0?`rgba(240,247,255,0.7)`:`rgba(248,250,252,0.7)`};
              border-radius: 8px;
              transition: background 0.2s;
            `,n.addEventListener(`mouseenter`,()=>n.style.background=`rgba(219,234,254,0.9)`),n.addEventListener(`mouseleave`,()=>n.style.background=t%2==0?`rgba(240,247,255,0.7)`:`rgba(248,250,252,0.7)`),n.innerHTML=`
              <div style="font-size: 13px; font-weight: 600; color: var(--apx-t-1e3a5f); margin-bottom: 4px; line-height: 1.3;">${e.title}</div>
              <div style="font-size: 11px; color: var(--apx-t-64748b);">${e.date}</div>
            `,d.appendChild(n)});let t=document.createElement(`a`);t.href=`https://aiub.edu/category/notices`,t.target=`_blank`,t.style.cssText=`
            display: block;
            padding: 12px;
            margin-top: 8px;
            text-align: center;
            font-size: 13px;
            font-weight: 700;
            color: var(--apx-t-1a73c8);
            text-decoration: none;
            background: rgba(26,115,200,0.06);
            border-radius: 8px;
            transition: background 0.2s;
          `,t.addEventListener(`mouseenter`,()=>t.style.background=`rgba(26,115,200,0.12)`),t.addEventListener(`mouseleave`,()=>t.style.background=`rgba(26,115,200,0.06)`),t.innerHTML=`See All Notices &rarr;`,d.appendChild(t)}else d.innerHTML=`<div style="padding: 20px; text-align: center; color: var(--apx-t-64748b); font-size: 13px;">No notices found.</div>`}catch(e){console.error(`Failed to load notices`,e)}},500),!document.getElementById(`aiub_custom_scrollbar_styles`)){let e=document.createElement(`style`);e.id=`aiub_custom_scrollbar_styles`,e.textContent=`
        /* Remove hover backgrounds for notification icons */
        .navbar-nav > li > a#noti_Button:hover,
        .navbar-nav > li > a#noti_Button:focus,
        .navbar-nav > li.open > a#noti_Button,
        .navbar-nav > li > a#aiub_custom_notices_btn:hover,
        .navbar-nav > li > a#aiub_custom_notices_btn:focus,
        .navbar-nav > li.open > a#aiub_custom_notices_btn {
          background-color: transparent !important;
          background: transparent !important;
        }

        /* Native Notification AngularJS Overrides */
        #notifications div[ng-repeat] .row {
          margin: 0 0 6px 0 !important;
          padding: 10px 12px !important;
          border-radius: 8px !important;
          color: var(--apx-t-1e3a5f) !important;
          transition: background 0.2s !important;
          border: none !important;
        }
        #notifications div[ng-repeat]:nth-child(even) .row {
          background: rgba(240,247,255,0.7) !important;
        }
        #notifications div[ng-repeat]:nth-child(odd) .row {
          background: rgba(248,250,252,0.7) !important;
        }
        #notifications div[ng-repeat] .row:hover {
          background: rgba(219,234,254,0.9) !important;
        }

        /* Thin light blue scrollbar for custom notices and native notifications */
        #aiub_custom_notices_dropdown::-webkit-scrollbar,
        #notifications .scrollable-menu::-webkit-scrollbar {
          width: 6px;
          height: 6px;
        }
        #aiub_custom_notices_dropdown::-webkit-scrollbar-track,
        #notifications .scrollable-menu::-webkit-scrollbar-track {
          background: rgba(240, 247, 255, 0.5); 
          border-radius: 8px;
        }
        #aiub_custom_notices_dropdown::-webkit-scrollbar-thumb,
        #notifications .scrollable-menu::-webkit-scrollbar-thumb {
          background: rgba(147, 197, 253, 0.9); 
          border-radius: 8px;
        }
        #aiub_custom_notices_dropdown::-webkit-scrollbar-thumb:hover,
        #notifications .scrollable-menu::-webkit-scrollbar-thumb:hover {
          background: rgba(96, 165, 250, 1);
        }
      `,document.head.appendChild(e)}}function t(e){e.style.cssText=`
      color: var(--apx-t-374151) !important;
      font-size: 14px !important;
      font-weight: 600 !important;
      font-family: system-ui,-apple-system,sans-serif !important;
      padding: 0 14px !important;
      height: 85px !important;
      display: flex !important;
      align-items: center !important;
      gap: 5px !important;
      transition: color 0.18s !important;
      text-decoration: none !important;
      white-space: nowrap !important;
    `,e.querySelectorAll(`.fa,.glyphicon`).forEach(e=>{e.style.cssText=`font-size: 13px !important; color: var(--apx-t-1a73c8) !important; transition: color 0.18s !important;`}),e.addEventListener(`mouseenter`,()=>{e.style.color=`var(--apx-t-1a73c8) !important`,e.querySelectorAll(`.fa,.glyphicon`).forEach(e=>e.style.color=`var(--apx-t-0d47a1) !important`)}),e.addEventListener(`mouseleave`,()=>{e.style.color=`var(--apx-t-374151) !important`,e.querySelectorAll(`.fa,.glyphicon`).forEach(e=>e.style.color=`var(--apx-t-1a73c8) !important`)})}function n(e,t,n,r=!1){e.style.cssText=`
      display: inline-flex !important;
      align-items: center !important;
      justify-content: center !important;
      padding: 0 10px !important;
      height: 85px !important;
      color: ${t} !important;
      text-decoration: none !important;
      border-radius: 0 !important;
      border: none !important;
      transition: background 0.18s !important;
      background: transparent !important;
      cursor: pointer !important;
      white-space: nowrap !important;
      vertical-align: middle !important;
    `,e.querySelectorAll(`.fa,.glyphicon`).forEach(e=>{e.style.cssText=`font-size: 18px !important; color: ${t} !important; display: inline-block !important;`}),e.querySelectorAll(`span:not(.fa):not(.glyphicon), .hidden-lg, .hidden-md, .hidden-sm, .hidden-xs, b, small`).forEach(e=>{e.style.display=`none !important`}),e.querySelectorAll(`.caret`).forEach(e=>{e.style.display=`none !important`});let i=t===`var(--apx-c-e53e3e)`?`rgba(229,62,62,0.08)`:`rgba(26,115,200,0.08)`;r||(e.addEventListener(`mouseenter`,()=>{e.style.background=i+` !important`}),e.addEventListener(`mouseleave`,()=>{e.style.background=`transparent !important`}))}})();