function sso(accessToken) {
    var urls = [""];
    for (var index in urls) {
        var url = urls[index];
        $.get(url, {accessToken: accessToken}, function (result) {
            console.log(result)
        }, "json");
    }
}