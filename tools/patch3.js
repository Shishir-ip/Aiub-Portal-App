const fs = require('fs');

// PortalController: withContext import + onFileChooserResult + setPendingFileChooser
let p = fs.readFileSync('app/src/main/java/com/shishir/aiubportal/portal/PortalController.kt', 'utf8');
if (!p.includes('import kotlinx.coroutines.withContext')) {
  p = p.replace('import kotlinx.coroutines.launch', 'import kotlinx.coroutines.launch\nimport kotlinx.coroutines.withContext');
}
if (!p.includes('fun onFileChooserResult')) {
  const anchor = '    private var pendingFileChooser: android.webkit.ValueCallback<Array<Uri>>? = null';
  const addition = anchor + '\n\n' +
    '    /** Used by the AIUB website tab\'s file chooser. */\n' +
    '    fun setPendingFileChooser(callback: android.webkit.ValueCallback<Array<Uri>>) {\n' +
    '        pendingFileChooser = callback\n' +
    '    }\n\n' +
    '    /** Called by MainActivity when the system picker returns. */\n' +
    '    fun onFileChooserResult(uris: Array<Uri>?) {\n' +
    '        pendingFileChooser?.onReceiveValue(uris ?: arrayOf())\n' +
    '        pendingFileChooser = null\n' +
    '    }';
  p = p.replace(anchor, addition);
}
fs.writeFileSync('app/src/main/java/com/shishir/aiubportal/portal/PortalController.kt', p);
console.log('PortalController ok:', p.includes('fun onFileChooserResult') && p.includes('withContext'));

// WebsiteScreen: accept controller param
let w = fs.readFileSync('app/src/main/java/com/shishir/aiubportal/WebsiteScreen.kt', 'utf8');
if (!w.includes('fun WebsiteScreen(controller')) {
  w = w.replace('fun WebsiteScreen() {', 'fun WebsiteScreen(controller: com.shishir.aiubportal.portal.PortalController) {');
  fs.writeFileSync('app/src/main/java/com/shishir/aiubportal/WebsiteScreen.kt', w);
}
console.log('WebsiteScreen ok:', w.includes('WebsiteScreen(controller'));

// MainActivity: onNoticesChanged callback no longer used by AppShell? keep; check WebsiteScreen call
let m = fs.readFileSync('app/src/main/java/com/shishir/aiubportal/MainActivity.kt', 'utf8');
if (!m.includes('WebsiteScreen(controller = controller)')) {
  m = m.replace('composable("website") { WebsiteScreen() }', 'composable("website") { WebsiteScreen(controller = controller) }');
  fs.writeFileSync('app/src/main/java/com/shishir/aiubportal/MainActivity.kt', m);
}
console.log('MainActivity ok:', m.includes('WebsiteScreen(controller = controller)'));
