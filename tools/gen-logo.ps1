Add-Type -AssemblyName System.Drawing

$outRoot = "C:\Users\SHISHIR\Downloads\Aiub-Portal-App\app\src\main\res"
$sizes = @{ "mdpi" = 108; "hdpi" = 162; "xhdpi" = 216; "xxhdpi" = 324; "xxxhdpi" = 432 }

foreach ($k in $sizes.Keys) {
    $s = $sizes[$k]
    $bmp = New-Object System.Drawing.Bitmap($s, $s)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
    $g.Clear([System.Drawing.Color]::Transparent)

    # white rounded border ring, inside the adaptive-icon safe zone (center 66%)
    $inset = $s * 0.19
    $r = $s * 0.07
    $x0 = $inset; $y0 = $inset
    $w = $s - 2 * $inset
    $h = $s - 2 * $inset
    $path = New-Object System.Drawing.Drawing2D.GraphicsPath
    $path.AddArc($x0, $y0, 2 * $r, 2 * $r, 180, 90)
    $path.AddArc($x0 + $w - 2 * $r, $y0, 2 * $r, 2 * $r, 270, 90)
    $path.AddArc($x0 + $w - 2 * $r, $y0 + $h - 2 * $r, 2 * $r, 2 * $r, 0, 90)
    $path.AddArc($x0, $y0 + $h - 2 * $r, 2 * $r, 2 * $r, 90, 90)
    $path.CloseFigure()
    $ringPen = New-Object System.Drawing.Pen([System.Drawing.Color]::White, ($s * 0.028))
    $g.DrawPath($ringPen, $path)

    # AIUB wordmark, centered in the border
    $font = New-Object System.Drawing.Font('Arial', ($w * 0.30), [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
    $sf = New-Object System.Drawing.StringFormat
    $sf.Alignment = [System.Drawing.StringAlignment]::Center
    $sf.LineAlignment = [System.Drawing.StringAlignment]::Center
    $textRect = New-Object System.Drawing.RectangleF($x0, $y0, $w, $h)
    $g.DrawString('AIUB', $font, [System.Drawing.Brushes]::White, $textRect, $sf)

    $g.Dispose()
    $dir = Join-Path $outRoot ("mipmap-" + $k)
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }
    $bmp.Save((Join-Path $dir "ic_launcher_foreground.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Output ("wrote " + $dir + "\ic_launcher_foreground.png")
}
