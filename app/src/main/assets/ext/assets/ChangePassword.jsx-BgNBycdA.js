import{o as e}from"./chunk-zsgVPwQN.js";import{n as t}from"./react-dom-C2Cg8Z4w.js";import{n,t as r}from"./jsx-runtime-C3SrMh-X.js";import{C as i,_ as a,v as o}from"./fi-m_7rYGJu.js";var s=e(n(),1);t();var c=r();chrome.storage.sync.get({extensionEnabled:!0,featureToggles:{}},e=>{if(!e.extensionEnabled||e.featureToggles?.profile===!1)return;let t=setInterval(()=>{let e=document.querySelector(`#main-content`),n=e?e.querySelector(`.panel`):null;n&&(clearInterval(t),l(n))},100)});function l(e){let t=e.querySelector(`.panel-heading`),n=e.querySelector(`.panel-body`),r=e.querySelector(`form`);if(!r)return;if(e.style.cssText=`
    background: var(--apx-c-ffffff);
    border: 1px solid var(--apx-c-e2e8f0);
    border-radius: 16px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
    overflow: hidden;
    margin-bottom: 24px;
  `,t){t.style.cssText=`
      background: linear-gradient(to right, var(--apx-c-f8fafc), var(--apx-c-ffffff));
      border-bottom: 1px solid var(--apx-c-e2e8f0);
      padding: 20px 24px;
    `;let e=t.querySelector(`.panel-title`);e&&(e.style.cssText=`
        color: var(--apx-t-0f172a);
        font-size: 18px;
        font-weight: 700;
        font-family: system-ui, -apple-system, sans-serif;
        margin: 0;
      `)}n&&(n.style.cssText=`
      padding: 32px 24px;
    `),r.querySelectorAll(`.form-group`).forEach(e=>{e.style.cssText=`
      margin-bottom: 20px;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
    `;let t=e.querySelector(`label`);t&&(t.style.cssText=`
        color: var(--apx-t-334155);
        font-size: 14px;
        font-weight: 600;
        margin-bottom: 8px;
        width: 100%;
        text-align: left;
        font-family: system-ui, -apple-system, sans-serif;
      `,t.className=``);let n=e.querySelector(`div[class^="col-"]`);if(n){n.className=``,n.style.cssText=`
        width: 100%;
      `;let e=n.querySelector(`input:not([type="hidden"]):not([type="submit"])`);if(e){e.name===`OldPassword`?e.placeholder=`Enter current password`:e.name===`NewPassword`?e.placeholder=`Enter new password`:e.name===`ConfirmPassword`&&(e.placeholder=`Confirm new password`);let t=document.createElement(`div`);if(t.style.cssText=`
          position: relative;
          width: 100%;
        `,e.parentNode.insertBefore(t,e),t.appendChild(e),e.style.cssText=`
          width: 100%;
          padding: 12px 48px 12px 16px; /* Extra padding on right for the eye icon */
          border: 1px solid var(--apx-c-cbd5e1);
          border-radius: 10px;
          font-size: 15px;
          color: var(--apx-t-1e293b);
          background-color: var(--apx-c-f8fafc);
          transition: all 0.2s ease-in-out;
          font-family: system-ui, -apple-system, sans-serif;
          height: auto;
          box-shadow: none;
        `,e.addEventListener(`focus`,()=>{e.style.borderColor=`var(--apx-t-3b82f6)`,e.style.backgroundColor=`var(--apx-t-ffffff)`,e.style.boxShadow=`0 0 0 4px rgba(59, 130, 246, 0.1)`}),e.addEventListener(`blur`,()=>{e.style.borderColor=`var(--apx-t-cbd5e1)`,e.style.backgroundColor=`var(--apx-t-f8fafc)`,e.style.boxShadow=`none`}),e.type===`password`){let n=document.createElement(`button`);n.type=`button`,n.style.cssText=`
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: transparent;
            border: none;
            padding: 4px;
            cursor: pointer;
            color: var(--apx-t-64748b);
            display: flex;
            align-items: center;
            justify-content: center;
            transition: color 0.2s;
            outline: none;
          `,n.addEventListener(`mouseenter`,()=>n.style.color=`var(--apx-t-3b82f6)`),n.addEventListener(`mouseleave`,()=>n.style.color=`var(--apx-t-64748b)`),t.appendChild(n);let r=(0,s.createRoot)(n),i=!1,l=({visible:e})=>e?(0,c.jsx)(o,{size:20}):(0,c.jsx)(a,{size:20});r.render((0,c.jsx)(l,{visible:i})),n.addEventListener(`click`,()=>{i=!i,e.type=i?`text`:`password`,r.render((0,c.jsx)(l,{visible:i}))})}}let t=n.querySelector(`span.field-validation-valid, span.field-validation-error`);t&&(t.style.cssText=`
          display: block;
          margin-top: 6px;
          font-size: 12px;
          color: var(--apx-t-ef4444);
          font-weight: 500;
        `)}});let l=r.querySelector(`input[type="submit"]`)?.closest(`.form-group`);if(l){l.style.cssText=`
      margin-top: 32px;
      display: flex;
      align-items: center;
      gap: 16px;
    `;let e=l.querySelector(`div[class^="col-"]`);if(e){e.className=``,e.style.cssText=`
        width: 100%;
        display: flex;
        align-items: center;
        gap: 16px;
      `;let t=e.querySelector(`input[type="submit"]`);t&&(t.className=``,t.style.cssText=`
          background: linear-gradient(135deg, var(--apx-c-2563eb), var(--apx-c-1d4ed8));
          color: var(--apx-t-ffffff);
          border: none;
          padding: 12px 24px;
          border-radius: 10px;
          font-size: 15px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s ease;
          box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
          font-family: system-ui, -apple-system, sans-serif;
        `,t.addEventListener(`mouseenter`,()=>{t.style.transform=`translateY(-1px)`,t.style.boxShadow=`0 6px 16px rgba(37, 99, 235, 0.3)`}),t.addEventListener(`mouseleave`,()=>{t.style.transform=`translateY(0)`,t.style.boxShadow=`0 4px 12px rgba(37, 99, 235, 0.2)`}));let n=e.querySelector(`a.tooltip-password-policy`);if(n){n.style.cssText=`
          color: var(--apx-t-64748b);
          font-size: 14px;
          font-weight: 500;
          text-decoration: none;
          transition: color 0.2s;
          display: inline-flex;
          align-items: center;
          gap: 6px;
        `;let e=n.textContent;n.textContent=``;let t=document.createElement(`span`);t.style.display=`flex`,t.style.alignItems=`center`;let r=document.createElement(`span`);r.textContent=e,n.appendChild(t),n.appendChild(r),(0,s.createRoot)(t).render((0,c.jsx)(i,{size:16})),n.addEventListener(`mouseenter`,()=>n.style.color=`var(--apx-t-3b82f6)`),n.addEventListener(`mouseleave`,()=>n.style.color=`var(--apx-t-64748b)`)}Array.from(e.childNodes).forEach(e=>{e.nodeType===3&&e.textContent.trim()===``&&e.remove()})}}}