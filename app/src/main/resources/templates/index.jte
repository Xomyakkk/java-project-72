@param String url = ""
@param String flash = null
@param String flashType = null

@template.layout(
    flash = flash,
    flashType = flashType,
    content = @`
    <div class="row justify-content-center">
        <div class="col-12 col-md-10 col-lg-8">
            <div class="border rounded-3 bg-light p-5 shadow-sm">
                <h1 class="display-4 mb-3">Анализатор страниц</h1>
                <p class="lead mb-4">Бесплатно проверяйте сайты на SEO-пригодность</p>

                <form action="/urls" method="post" class="row g-3">
                    <div class="col-12 col-md-8">
                        <label for="url" class="visually-hidden">URL для проверки</label>
                        <input
                            id="url"
                            type="text"
                            name="url"
                            class="form-control form-control-lg"
                            placeholder="https://www.example.com"
                            value="${url}"
                        >
                    </div>
                    <div class="col-12 col-md-4 d-grid">
                        <input type="submit" class="btn btn-primary btn-lg text-uppercase" value="Проверить">
                    </div>
                </form>
            </div>
        </div>
    </div>
`,
    footer = @`
        <a href="https://hexlet.io">Hexlet</a>
    `
)
