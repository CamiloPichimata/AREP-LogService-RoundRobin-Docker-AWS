function saveString() {
    console.log("Click en save string");
    var string = document.getElementById("GET-String").value;
    console.log("String: " + string);
    var date = new Date(Date.now()).toLocaleDateString();
    console.log("date: " + date);
    var data = '{"string":"' + string + '", "date":"' + date + '"}'
    console.log("data: " + data + " -> tipo: " + typeof data);

    axios.post('/post', data)
        .then(axios.get('/get')
            .then(response => {
                var rta = JSON.parse(response.data);
            }).catch(function(error) {
                console.log("Se ha obtenido un error cuando se estaba obtenido la lista de los registros");
                console.log(error);
            }))
        .catch(function(error) {
            console.log("Se ha presentado un problema durante la transacióm")
            console.log(error);
        });
}
