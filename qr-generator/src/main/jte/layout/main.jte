@param gg.jte.Content content

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src="https://unpkg.com/@tailwindcss/browser@4"></script>
    <script src="https://unpkg.com/htmx.org@2.0.4"></script>
    <script defer src="/custom.js"></script>
    <link href="/custom.css" rel="stylesheet">
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
    <meta charset="UTF-8">
    <meta content="stella6767" name="author">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
</head>
<body id="body" hx-indicator=".loading-bar">
<div class="loading-bar"></div>

<div id="error-alert-container"></div>

${content}

</body>
</html>
