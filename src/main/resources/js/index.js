/** Созаем данных для персон, с функцией вычисления возраста **/
var Person = {
    id: "",
    firstName: "",
    lastName: "",
    city: "",
    dataR: "",
    age() {
        let dp = this.dataR.split("-"); // разделяем дату по точкам на массив
        let today = new Date();
        let year = today.getFullYear();
        return (year - dp[0]);
    }
};

/** Созаем массив в котором хранятся персоны **/
var arrPerson = [];

/** Div c полупрозрачным фоном **/
var bgTransparent = document.getElementById("AllScreenTransparent");

/** Div c полупрозрачным фоном **/
var formAddPerson = document.getElementById("AllScreen");

/** Div c блоком вопроса **/
var qBox = document.getElementById("QuestionBox");

/**
 * Функция выполнения запроса AJAX
 * @param method - GET или POST
 * @param url - файл от которого получаем запрос
 * @param body - параметры для метода пост в формате JSON
 * @returns {Promise<unknown>} - возвращаемые данные
 */
function sendRequest(method, url, body = null) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();

        xhr.open(method, url);

        xhr.responseType = 'json';
        xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');

        xhr.onload = () => {
            if (xhr.status >= 400) {
                reject(xhr.response);
            } else {
                resolve(xhr.response);
            }
        }

        xhr.onerror = () => {
            reject(xhr.response);
        }

        if ( body === null ) {
            xhr.send();
        } else {
            xhr.send(JSON.stringify(body));
        }

    });
}

/**
 * Функция копирования полученных данных в массив персон
 * @param data - полученные данные
 */
function copyDownloadPersonInArray( data ) {
    arrPerson.splice(0, arrPerson.length);
    for (var i = 0; i < data.length; i++) {
        let p = {};
        p.__proto__ = Person;
        p.id = data[i].id;
        p.firstName = data[i].firstName;
        p.lastName = data[i].lastName;
        p.city = data[i].city;
        p.dataR = data[i].dataR;
        arrPerson.push(p);
    }

    AllPersonToDiv(); // обновим див блок
}

/**
 * Функция вывода всех персон в таблицу DIV блока
 **/
function AllPersonToDiv() {
    let html = "";
    html = "<table class='table_3' style='margin-top: 50px;'>";
    html += "<tr class='zagolovok'><td>№</td><td>ФИО</td><td>Город</td><td>Дата рождения</td><td>Возраст</td><td>Операции</td></tr>";


    for (var i = 0; i < arrPerson.length; i++) {
        html += "<tr><td>" + (i + 1) + "</td><td class='txtAlignLeft'>" + arrPerson[i].firstName + " " + arrPerson[i].lastName +
            "</td><td>" + arrPerson[i].city + "</td><td>" + reverseDate(arrPerson[i].dataR) +
            "</td><td>" + arrPerson[i].age() + "</td><td><img src='/img/edit.png' class='button' onclick='editPerson(" + i +");'>" +
            "<img src='/img/delete.png' class='button' onclick='deletePerson(" + i +");'></td></tr>";
    }
    if (arrPerson.length==0) {
        html += "<tr><td colspan='5'>В массиве нет записей</td></tr>";
    }
    html += "</table>";

    document.getElementById("person").innerHTML = html;
}

/**
 * Функция нажаитя кнопки на форме добавления персоны
 */
function cancelPerson() {
    bgTransparent.style.display="none";
    formAddPerson.style.display="none";
}
/**
 * Функция реверса даты
 */
function reverseDate(data) {
    data = data.split("-");
    return data[2]+"."+data[1]+"."+data[0];
}

/**
 * Функция открытия формы для редактирования строки
 * @param i
 */
function editPerson(i) {
    document.getElementById("error").innerHTML = "";
    document.getElementById("index_person").value = i;
    document.getElementById("addPerson").value = "Редактировать";
    document.getElementById("formZagolovok").innerHTML = "Редактирование персоны";
    document.getElementById("id").value = arrPerson[i].id;
    document.getElementById("firstName").value = arrPerson[i].firstName;
    document.getElementById("lastName").value = arrPerson[i].lastName;
    document.getElementById("city").value = arrPerson[i].city;
    document.getElementById("dataR").value = arrPerson[i].dataR;

    bgTransparent.style.display="block";
    formAddPerson.style.display="block";
}
/**
 * Функция открытия формы добавления персоны
 */
function openFormPerson() {
    document.getElementById("error").innerHTML = "";
    document.getElementById("index_person").value = "-1";
    document.getElementById("id").value = "-1";
    document.getElementById("addPerson").value = "Добавить";
    document.getElementById("formZagolovok").innerHTML = "Добавление персоны";
    document.getElementById("firstName").value = "";
    document.getElementById("lastName").value = "";
    document.getElementById("city").value = "";
    document.getElementById("dataR").value = "2000-01-01";
    bgTransparent.style.display="block";
    formAddPerson.style.display="block";
}
/**
 *  Удаление персоны по индексу из массива
 * @param i - индекс массива
 */
function deletePerson(i) {
    QustionBox("Запрос на удаление", "Вы действительно хотите удалить \"" + arrPerson[i].firstName + " " +arrPerson[i].lastName +
        " (" + arrPerson[i].city + ")" + "\"?", () => {
        arrPerson[i].id = String(arrPerson[i].id);
        sendRequest("POST", "http://localhost/showdb?persons=delete", arrPerson[i])
            .then(data => {
                let err = true;
                if (data !== null) {
                    if (data["deleted_id"] !== undefined) {
                        if (data["deleted_id"] !== "-1") {
                            err = false;
                            console.log("Удалена запись в БД с индексом id = " + data["deleted_id"]);
                        }
                    }
                }
                if (err) {
                    alert("В процессе удаления записи в БД произошла ошибка!");
                    console.log(data);
                }
            })
        arrPerson.splice(i, 1);
        AllPersonToDiv();
        qBox.style.display = "none";
        bgTransparent.style.display = "none";
    }, () => {
        qBox.style.display = "none";
        bgTransparent.style.display = "none";
    });


}

/**
 * Функция добавления в массив
 **/
function addPerson() {
    // проверка корректности ввода данных
    var err = document.getElementById("error");
    let p = {};

    p.__proto__ = Person;
    p.id= document.getElementById("id").value;
    p.firstName = document.getElementById("firstName").value;
    p.lastName = document.getElementById("lastName").value;
    p.city = document.getElementById("city").value;
    p.dataR = document.getElementById("dataR").value;

    if (p.firstName.length < 1) {
        err.innerHTML = "Введите <b>Имя</b> человека <br>";
        return;
    }

    if (p.lastName.length < 1) {
        err.innerHTML = "Введите <b>Фамилию</b> человека <br>";
        return;
    }

    if (p.city.length < 1) {
        err.innerHTML = "Введите <b>Город</b> человека <br>";
        return;
    }
    var index_person = Number(document.getElementById("index_person").value);
    if (index_person == -1) {
        sendRequest("POST", "http://localhost/showdb?persons=add", p)
            .then(data => {

                let err = true;
                if (data !== null) {
                    if (data["insert_id"] !== undefined) {
                        if (data["insert_id"] !== "-1") {
                            p.id = data["insert_id"];
                            arrPerson.push(p);
                            AllPersonToDiv();
                            err = false;
                            console.log("Создана в БД запись с индексом id = " + data["insert_id"]);
                        }
                    }
                }
                if (err) {
                    alert("В процессе создания записи в БД произошла ошибка!");
                    console.log(data);
                }

            })

    } else {
        arrPerson[index_person].id = p.id;
        arrPerson[index_person].firstName = p.firstName;
        arrPerson[index_person].lastName = p.lastName;
        arrPerson[index_person].city = p.city;
        arrPerson[index_person].dataR = p.dataR;
        sendRequest("POST", "http://localhost/showdb?persons=edit", p)
            .then(data => {
                let err = true;
                if (data !== null) {
                    if (data["updated_id"] !== undefined) {
                        if (data["updated_id"] !== "-1") {
                            err = false;
                            console.log("Изменена запись в БД с индексом id = " + data["updated_id"]);
                        }
                    }
                }
                if (err) {
                    alert("В процессе изменения записи в БД произошла ошибка!");
                    console.log(data);
                }
            })
    }

    cancelPerson();
    AllPersonToDiv(); // обновим див блок

}

function QustionBox (qTitle, qText, fYes, fNo) {
    bgTransparent.style.display = "block";
    document.getElementById("QuestionTitle").innerHTML = qTitle;
    document.getElementById("QuestionText").innerHTML = qText;
    qBox.style.display = "block";
    document.getElementById("QuestionButtonYes").onclick = fYes;
    document.getElementById("QuestionButtonNo").onclick = fNo;
}

function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
        currentDate = Date.now();
    } while (currentDate - date < milliseconds);
}


/**
 * Выполняем запрос к файлу с данными в формате JSON, при удачном выполнении запроса выволняем
 * функцию копирования загруженных данных в массив
 */

function refreshPerson(){
    let html = "";
    html = "<table class='table_3' style='margin-top: 50px;'>";
    html += "<tr class='zagolovok'><td>№</td><td>ФИО</td><td>Город</td><td>Дата рождения</td><td>Возраст</td><td>Операции</td></tr>";
    html += "<tr><td colspan=\"6\" class=\"colspan5\"> <img src=\"/img/loading.gif\" width=\"32\"> Идет загрузка данных с сервера...</td></tr>";
    html += "</table>";
    document.getElementById("person").innerHTML = html;

   // sleep(5000);
    setTimeout( () => {
        sendRequest("GET", "http://localhost/showdb")
            .then(data => {copyDownloadPersonInArray(data);})
        }, 5000);

}