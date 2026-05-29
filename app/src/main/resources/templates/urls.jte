@import java.util.List
@import hexlet.code.model.Url

@param List<Url> urls
@param String flash = null

@template.layout(
    flash = flash,
    content = @`
    <div class="card shadow-sm">
        <div class="card-body">
            <h1 class="h2 mb-4">Сайты</h1>
            <table data-test="urls" class="table table-striped align-middle mb-0">
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
                            <td>${url.getCreatedAt().toLocalDateTime().toLocalDate()}</td>
                            <td>-</td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    </div>
`,
    footer = @`
        <a href="https://hexlet.io">Hexlet</a>
    `
)
