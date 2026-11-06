import{o as e}from"./chunk-zsgVPwQN.js";import{n as t,t as n}from"./jsx-runtime-C3SrMh-X.js";/* empty css                */var r=e(t(),1),i=n();function a(){let e=document.querySelector(`.navbar-text .navbar-link small`)||document.querySelector(`.navbar-text .navbar-link`);if(!e)return``;let t=e.textContent.trim(),n=t.split(`,`).map(e=>e.trim()),r=e=>e.toLowerCase().replace(/\b\w/g,e=>e.toUpperCase());return n.length>=2?r(n[1])+` `+r(n[0]):r(t)}function o(){let e=new Date().getHours();return e<12?`Good Morning`:e<17?`Good Afternoon`:`Good Evening`}function s({name:e}){let t=e.split(` `)[0]||`Student`,n=new Date().toLocaleDateString(`en-US`,{weekday:`long`,year:`numeric`,month:`long`,day:`numeric`});return(0,i.jsxs)(`div`,{style:{marginBottom:20,paddingBottom:16,borderBottom:`2px solid var(--apx-c-dbeafe)`,fontFamily:`system-ui,-apple-system,sans-serif`},children:[(0,i.jsxs)(`h2`,{style:{fontSize:22,fontWeight:800,color:`var(--apx-t-0f172a)`,margin:`0 0 4px`,lineHeight:1.3},children:[o(),`,`,` `,(0,i.jsx)(`span`,{style:{background:`linear-gradient(135deg,var(--apx-c-0284c7),var(--apx-c-06b6d4))`,WebkitBackgroundClip:`text`,WebkitTextFillColor:`transparent`,backgroundClip:`text`},children:t}),`!`]}),(0,i.jsx)(`p`,{style:{fontSize:13,color:`var(--apx-t-94a3b8)`,margin:0},children:n})]})}function c(e){let t=e.querySelector(`.alert.alert-success`);if(t){t.style.cssText=`
      background: linear-gradient(180deg, var(--apx-c-f8fafc) 0%, var(--apx-c-e0e7ff) 100%) !important;
      border: 1px solid rgba(37, 99, 235, 0.24) !important;
      border-left: 4px solid var(--apx-c-2563eb) !important;
      border-radius: 12px !important;
      box-shadow: 0 4px 10px rgba(30, 58, 138, 0.08) !important;
      padding: 16px !important;
      color: var(--apx-t-1f2937) !important;
      margin-bottom: 14px !important;
    `;let e=t.querySelector(`:scope > b`);e&&(e.style.cssText=`
        display: block !important;
        color: var(--apx-t-1e3a8a) !important;
        font-size: 14px !important;
        font-weight: 700 !important;
        margin-bottom: 12px !important;
      `);let n=t.querySelector(`table.table`);n&&(n.style.cssText=`
        margin-bottom: 0 !important;
        border: 1px solid var(--apx-c-dbeafe) !important;
        border-radius: 10px !important;
        overflow: hidden !important;
        background: var(--apx-ov-90) !important;
      `,n.querySelectorAll(`td`).forEach((e,t)=>{let n=t%3==2;e.style.cssText=`
          border-color: var(--apx-c-e2e8f0) !important;
          padding: 10px 12px !important;
          color: ${n?`var(--apx-t-0f172a)`:`var(--apx-t-1e3a8a)`} !important;
          font-size: 13px !important;
          font-weight: ${n?`700`:`600`} !important;
          background: transparent !important;
        `}));let r=t.querySelector(`.thumbnail`);if(r){r.style.cssText=`
        margin-bottom: 0 !important;
        border-radius: 10px !important;
        border: 1px solid var(--apx-c-dbeafe) !important;
        background: var(--apx-ov-88) !important;
        box-shadow: none !important;
        padding: 10px 12px !important;
      `;let e=r.querySelector(`.caption`);e&&(e.style.cssText=`
          color: var(--apx-t-1e3a8a) !important;
          font-size: 13px !important;
          margin-bottom: 10px !important;
        `)}let i=t.querySelector(`a.btn`);i&&(i.style.cssText=`
        border: none !important;
        border-radius: 8px !important;
        background: linear-gradient(135deg, var(--apx-c-1e3a8a) 0%, var(--apx-c-2563eb) 100%) !important;
        color: var(--apx-t-ffffff) !important;
        font-weight: 700 !important;
        padding: 8px 14px !important;
        text-decoration: none !important;
      `)}let n=e.querySelector(`.alert.alert-info`);if(n){n.style.cssText=`
      background: linear-gradient(180deg, var(--apx-c-f8fafc) 0%, var(--apx-c-e0e7ff) 100%) !important;
      border: 1px solid rgba(59, 130, 246, 0.24) !important;
      border-left: 4px solid var(--apx-c-2563eb) !important;
      border-radius: 12px !important;
      box-shadow: 0 4px 10px rgba(30, 58, 138, 0.06) !important;
      color: var(--apx-t-1f2937) !important;
      padding: 16px !important;
      margin-bottom: 14px !important;
      line-height: 1.6 !important;
    `,n.querySelectorAll(`b`).forEach(e=>{e.style.color=`var(--apx-t-1e3a8a)`,e.style.fontWeight=`700`});let e=n.querySelector(`a`);if(e){e.style.cssText=`
        display: inline-block !important;
        border: none !important;
        border-radius: 6px !important;
        background: linear-gradient(135deg, var(--apx-c-1e3a8a) 0%, var(--apx-c-2563eb) 100%) !important;
        color: var(--apx-t-ffffff) !important;
        font-weight: 700 !important;
        padding: 4px 12px !important;
        text-decoration: none !important;
        margin-left: 4px !important;
        box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2) !important;
      `;let t=e.querySelector(`b`);t&&(t.style.color=`var(--apx-t-ffffff)`)}}let r=e.querySelector(`.alert.alert-warning`);r&&(r.style.cssText=`
      background: linear-gradient(180deg, var(--apx-c-f8fafc) 0%, var(--apx-c-e0e7ff) 100%) !important;
      border: 1px solid rgba(59, 130, 246, 0.24) !important;
      border-left: 4px solid var(--apx-c-2563eb) !important;
      border-radius: 12px !important;
      box-shadow: 0 4px 10px rgba(30, 58, 138, 0.06) !important;
      color: var(--apx-t-1f2937) !important;
      padding: 14px 16px !important;
      margin-bottom: 14px !important;
    `,r.querySelectorAll(`b`).forEach(e=>{e.style.color=`var(--apx-t-1f2937)`,e.style.fontWeight=`600`}),r.querySelectorAll(`a`).forEach(e=>{e.style.color=`var(--apx-t-1d4ed8)`,e.style.fontWeight=`700`,e.style.textDecoration=`underline`}));let i=e.querySelector(`.panel.panel-default`);if(i){i.style.cssText=`
      background: linear-gradient(180deg, var(--apx-c-f8fafc) 0%, var(--apx-c-e0e7ff) 100%) !important;
      border: 1px solid rgba(37, 99, 235, 0.2) !important;
      border-radius: 12px !important;
      box-shadow: 0 4px 10px rgba(30, 58, 138, 0.07) !important;
      overflow: hidden !important;
      margin-bottom: 14px !important;
    `;let e=i.querySelector(`.panel-body`);e&&(e.style.cssText=`padding: 16px !important; background: transparent !important;`);let t=i.querySelector(`.text-center`);t&&(t.style.cssText=`display: flex !important; flex-wrap: wrap !important; gap: 10px !important; justify-content: center !important;`),i.querySelectorAll(`.text-center a.btn`).forEach((e,t)=>{let n=t===0;e.style.cssText=`
        border: none !important;
        border-radius: 8px !important;
        background: ${n?`linear-gradient(135deg, var(--apx-c-1e3a8a) 0%, var(--apx-c-2563eb) 100%)`:`linear-gradient(135deg, var(--apx-c-0ea5e9) 0%, var(--apx-c-2563eb) 100%)`} !important;
        color: var(--apx-t-ffffff) !important;
        font-weight: 700 !important;
        font-size: 12.5px !important;
        padding: 9px 14px !important;
        text-decoration: none !important;
        box-shadow: 0 2px 8px rgba(37, 99, 235, 0.2) !important;
      `})}}(function(){window.__aiubIntroMounted||chrome.storage.sync.get({extensionEnabled:!0,featureToggles:{}},e=>{if(!e.extensionEnabled||e.featureToggles?.generalUI===!1)return;function t(){let e=document.getElementById(`main-content`);if(!e){setTimeout(t,200);return}if(window.__aiubIntroMounted)return;window.__aiubIntroMounted=!0;let n=e.querySelector(`.row > .col-sm-12`)||e.querySelector(`.row > .col-xs-12`)||e.querySelector(`.row > [class*="col-"]`);if(!n)return;let o=document.createElement(`div`);o.id=`aiub-intro-root`,n.insertBefore(o,n.firstChild),(0,r.createRoot)(o).render((0,i.jsx)(s,{name:a()})),c(n)}document.readyState===`loading`?document.addEventListener(`DOMContentLoaded`,t):t()})})();