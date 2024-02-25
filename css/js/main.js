function editButtonPress(id) {
    let container = id.parentElement
    if (container.children[0].children[0].getAttribute('readonly') == 'readonly'){
        container.children[0].children[0].removeAttribute('readonly');
        container.children[1].children[0].removeAttribute('readonly');
        container.children[2].children[0].src = 'png/save.png';
        } else {
        container.children[0].children[0].setAttribute('readonly', 'readonly');
        container.children[1].children[0].setAttribute('readonly', 'readonly');
        container.children[2].children[0].src = 'png/pencil.png';
        }
}

function deleteButtonPress(id) {
    let delElement = id.parentElement;
//    delElement.style.display = 'none';
//    delElement.id = "delete"
    delElement.parentNode.removeChild(delElement)
}

function addButtonPress() {
    let count = parseInt(document.getElementById('counter').value) + 1;
    document.getElementById('counter').value = count;
    let template = document.getElementById('template');
    let new_element = template.cloneNode(true);
    new_element.style.display = '';
    new_element.children[0].children[0].name = 'name' + count;
    new_element.children[1].children[0].name = 'tgid' + count;
    new_element.children[5].name = 'id' + count;
    new_element.children[5].value = count;
    editButtonPress(new_element.children[0])
    let del = new_element.getElementsByTagName('style')[0]
    del.parentNode.removeChild(del)
    new_element.id = "new"
    template.after(new_element);
}

function setWorkerIdCheckbox(id) {
    var c = document.querySelector('#' + id);
    c.checked = !c.checked;
    const text = c.parentElement.children[1];
    if (c.checked) {
        var new_text = text.innerHTML.replace('выкл.', 'вкл.');
    } else {
        var new_text = text.innerHTML.replace('вкл.', 'выкл.');
    }
    text.innerHTML = new_text;
}

function setCheckbox(id) {
    var c = document.querySelector('#' + id);
    c.checked = !c.checked;
}

function editOnLoad() {
    var select = document.getElementsByName('sendWhenType')[0]
    onSelectWhenType(select)
}

function onSelectWhenType(select) { //1 - периодически, 2 - дни недели, 3 - числа месяца, 0 - ежедневно, 4 - В указанные даты
    var selectedOption = select.options[select.selectedIndex]
    console.log('Выбор: >' + selectedOption.value + '<')

    switch(selectedOption.value) {
        case '0':
            document.getElementById('sendPeriod').style.display = 'none';
            document.getElementById('sendTime').style.display = '';
            document.getElementById('sendWeekDay').style.display = 'none';
            document.getElementById('sendMonthDay').style.display = 'none';
            document.getElementById('sendDateTime').style.display = 'none';
            break;
        case '1':
            document.getElementById('sendPeriod').style.display = '';
            document.getElementById('sendTime').style.display = 'none';
            document.getElementById('sendWeekDay').style.display = 'none';
            document.getElementById('sendMonthDay').style.display = 'none';
            document.getElementById('sendDateTime').style.display = 'none';
        break;
        case '2':
            document.getElementById('sendPeriod').style.display = 'none';
            document.getElementById('sendTime').style.display = '';
            document.getElementById('sendWeekDay').style.display = '';
            document.getElementById('sendMonthDay').style.display = 'none';
            document.getElementById('sendDateTime').style.display = 'none';
        break;
        case '3':
            document.getElementById('sendPeriod').style.display = 'none';
            document.getElementById('sendTime').style.display = '';
            document.getElementById('sendWeekDay').style.display = 'none';
            document.getElementById('sendMonthDay').style.display = '';
            document.getElementById('sendDateTime').style.display = 'none';
        break;
        case '4':
            document.getElementById('sendPeriod').style.display = 'none';
            document.getElementById('sendTime').style.display = 'none';
            document.getElementById('sendWeekDay').style.display = 'none';
            document.getElementById('sendMonthDay').style.display = 'none';
            document.getElementById('sendDateTime').style.display = '';
        break;
        default:
            console.log("Непредусмотренный вариант")
    }
}

function sendDateTimeClick(iconClickElement){
    let del = iconClickElement.parentElement;
    del.parentNode.removeChild(del)
}

function addDateTimeField(iconClickElement){
    let template = document.getElementById('sendDateTimeTemplate');
    let new_element = template.cloneNode(true);
    new_element.style.display = '';
    let del = new_element.getElementsByTagName('style')[0];
    del.parentNode.removeChild(del);
    new_element.id = "new";

    var currentDate = new Date();
    currentDate.setUTCHours(currentDate.getUTCHours() + 7);
    var localDate = currentDate.toISOString().split(':')
    var nowDate = localDate[0] + ':' + localDate[1]
    console.log(new_element);
    new_element.children[0].children[0].value = nowDate

    document.getElementById('sendDateTimePlus').before(new_element);
}

function hideBlock(listHeader) {
    var listStyle = listHeader.nextSibling.style;

    if (listStyle.display == '') {
        listStyle.display = 'none';
        listHeader.className = 'label list collapsed'; // >
    } else {
        listStyle.display = '';
        listHeader.className = 'label list expand'; // V
    }
}

function sendNow(element){
    var hrefElement = element.parentElement
    var workerId = hrefElement.getAttribute('title')    
    event.preventDefault();    

    const Http = new XMLHttpRequest();
    const url='/send-now?workerId=' + workerId;
    Http.open("GET", url);
    Http.send();
    
    Http.onreadystatechange = function() {
        if (Http.readyState === XMLHttpRequest.DONE && Http.status === 200) {
            console.log(Http.responseText);
            alert(JSON.parse(Http.responseText).message + ' (' + hrefElement.text + ')');
          }
    }
}
