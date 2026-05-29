@import hexlet.code.model.Url

@param Url url
@param String flash = null

@template.layout(
    flash = flash,
    content = @`
    <div class="card shadow-sm">
        <div class="card-body">
            <h1 class="h2 mb-4">${url.getName()}</h1>
            <table data-test="url" class="table table-bordered mb-0">
                <tbody>
                    <tr>
                        <td class="fw-semibold">ID</td>
                        <td>${url.getId()}</td>
                    </tr>
                    <tr>
                        <td class="fw-semibold">Имя</td>
                        <td>${url.getName()}</td>
                    </tr>
                    <tr>
                        <td class="fw-semibold">Дата создания</td>
                        <td>${url.getCreatedAt().toLocalDateTime().toLocalDate()}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
`,
    footer = @`
        <a href="https://hexlet.io">Hexlet</a>
    `
)
