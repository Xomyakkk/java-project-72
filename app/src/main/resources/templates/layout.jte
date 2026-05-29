@import gg.jte.Content

@param Content content
@param Content flash = null
@param Content footer = null

<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
</head>
<body class="d-flex flex-column min-vh-100 bg-white">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
        <div class="navbar-nav">
            <a class="nav-link" href="/urls">Сайты</a>
        </div>
    </div>
</nav>

<main class="container flex-grow-1 py-5">
    @if (flash != null)
        <div class="mb-4">
            ${flash}
        </div>
    @endif
    ${content}
</main>

@if (footer != null)
    <footer class="border-top py-3 mt-auto">
        <div class="container text-center">
            ${footer}
        </div>
    </footer>
@endif

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
</body>
</html>
