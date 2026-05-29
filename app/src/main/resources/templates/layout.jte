@import gg.jte.Content

@param Content content
@param Content flash = null

<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT3T4xg2YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous">
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
    </div>
</nav>

<main class="container py-5">
    @if (flash != null)
        <div class="mb-4">
            ${flash}
        </div>
    @endif
    ${content}
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5sqG7Y5n3zV9zzTtmI3UksdQRVvoxMfooAo0Pat4Ym" crossorigin="anonymous"></script>
</body>
</html>
