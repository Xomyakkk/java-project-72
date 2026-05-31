@import java.util.List
@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import hexlet.code.util.TextUtils

@param Url url
@param List<UrlCheck> checks
@param String flash = null
@param String flashType = null

@template.layout(
    flash = flash,
    flashType = flashType,
    content = @`
    <div class="mb-5">
        <h1 class="display-6 mb-4">Сайт: ${url.getName()}</h1>
        <table data-test="url" class="table table-bordered mb-0">
            <tbody>
                <tr>
                    <td>ID</td>
                    <td>${url.getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${url.getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${url.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                </tr>
            </tbody>
        </table>
    </div>

    <div>
        <h2 class="h1 mb-3">Проверки</h2>
        <form method="post" action="/urls/${url.getId()}/checks" class="mb-3">
            <input type="submit" class="btn btn-primary" value="Запустить проверку">
        </form>

        <table data-test="checks" class="table table-bordered">
            <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Код ответа</th>
                    <th scope="col">h1</th>
                    <th scope="col">title</th>
                    <th scope="col">description</th>
                    <th scope="col">Дата создания</th>
                </tr>
            </thead>
            <tbody>
                @for (UrlCheck check : checks)
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${TextUtils.truncate(check.getH1())}</td>
                        <td>${TextUtils.truncate(check.getTitle())}</td>
                        <td>${TextUtils.truncate(check.getDescription())}</td>
                        <td>${check.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                    </tr>
                @endfor
            </tbody>
        </table>
    </div>
`,
    footer = @`
        <a href="https://hexlet.io">Hexlet</a>
    `
)
