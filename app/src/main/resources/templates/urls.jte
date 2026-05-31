@import java.util.List
@import java.util.Map
@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck

@param List<Url> urls
@param Map<Long, UrlCheck> latestChecks
@param String flash = null
@param String flashType = null

@template.layout(
    flash = flash,
    flashType = flashType,
    content = @`
    <h1 class="display-6 mb-4">Сайты</h1>
    <table data-test="urls" class="table table-bordered align-middle">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Имя</th>
                <th scope="col">Дата создания</th>
                <th scope="col">Код ответа</th>
            </tr>
        </thead>
        <tbody>
            @for (Url url : urls)
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${url.getCreatedAt().toLocalDateTime().toLocalDate().toString()}</td>
                    <td>
                        @if (latestChecks.get(url.getId()) != null)
                            ${latestChecks.get(url.getId()).getStatusCode()}
                        @else
                            -
                        @endif
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>
`,
    footer = @`
        <a href="https://hexlet.io">Hexlet</a>
    `
)
