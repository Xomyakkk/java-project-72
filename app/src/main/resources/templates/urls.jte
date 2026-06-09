@import java.util.List
@import java.util.Map
@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import hexlet.code.util.DateTimeUtils

@param List<Url> urls
@param Map<Long, UrlCheck> latestChecks
@param String flash = null
@param String flashType = null

@template.layout(
    flash = flash,
    flashType = flashType,
    content = @`
    <h1 class="display-6 mb-4">&#1057;&#1072;&#1081;&#1090;&#1099;</h1>
    <table data-test="urls" class="table table-bordered align-middle">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">&#1048;&#1084;&#1103;</th>
                <th scope="col">&#1044;&#1072;&#1090;&#1072; &#1089;&#1086;&#1079;&#1076;&#1072;&#1085;&#1080;&#1103;</th>
                <th scope="col">&#1050;&#1086;&#1076; &#1086;&#1090;&#1074;&#1077;&#1090;&#1072;</th>
                <th scope="col">&#1044;&#1072;&#1090;&#1072; &#1087;&#1086;&#1089;&#1083;&#1077;&#1076;&#1085;&#1077;&#1081; &#1087;&#1088;&#1086;&#1074;&#1077;&#1088;&#1082;&#1080;</th>
            </tr>
        </thead>
        <tbody>
            @for (Url url : urls)
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${DateTimeUtils.format(url.getCreatedAt())}</td>
                    <td>
                        @if (latestChecks.get(url.getId()) != null)
                            ${latestChecks.get(url.getId()).getStatusCode()}
                        @else
                            -
                        @endif
                    </td>
                    <td>
                        @if (latestChecks.get(url.getId()) != null)
                            ${DateTimeUtils.format(latestChecks.get(url.getId()).getCreatedAt())}
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
