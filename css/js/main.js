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

function editOnLoad(sendWhenType) {
    switchSelect(sendWhenType)
    if (document.getElementById('preliminarySwitcher')) {
        preliminaryVisibility();
    }
}

function onSelectWhenType(select) { //0 - ежедневно, 1 - периодически, 2 - дни недели, 3 - числа месяца, 4 - В указанные даты
    var selectedOption = select.options[select.selectedIndex]    
    switchSelect(selectedOption.value)    
}

function switchSelect(sendWhenType){
    console.log('Выбор: >' + sendWhenType + '<');
    let sendPeriod = document.getElementById('sendPeriod');
    let sendTime = document.getElementById('sendTime');
    let sendWeekDay = document.getElementById('sendWeekDay');
    let sendMonthDay = document.getElementById('sendMonthDay');
    let sendDateTime = document.getElementById('sendDateTime');
    let preliminarySwitcher = document.getElementById('preliminarySwitcher');  
    
    switch(sendWhenType) {
        case '0':
            if (sendPeriod) sendPeriod.style.display = 'none';
            if (sendTime) sendTime.style.display = '';
            if (sendWeekDay) sendWeekDay.style.display = 'none';
            if (sendMonthDay) sendMonthDay.style.display = 'none';
            if (sendDateTime) sendDateTime.style.display = 'none';
            if (preliminarySwitcher) {
                if ((preliminarySwitcher.children[2].value.toLowerCase() !== 'true')) {
                    preliminarySwitcher.style.display = 'none';
                    preliminaryFieldsShow(false);
                } else preliminaryVisibility();
            }
            break;
        case '1':
            if (sendPeriod) sendPeriod.style.display = '';
            if (sendTime) sendTime.style.display = 'none';
            if (sendWeekDay) sendWeekDay.style.display = 'none';
            if (sendMonthDay) sendMonthDay.style.display = 'none';
            if (sendDateTime) sendDateTime.style.display = 'none';
            if (preliminarySwitcher) {
                if ((preliminarySwitcher.children[2].value.toLowerCase() !== 'true')) {
                    preliminarySwitcher.style.display = 'none';
                    preliminaryFieldsShow(false);
                } else preliminaryVisibility();
            }
        break;
        case '2':
            if (sendPeriod) sendPeriod.style.display = 'none';
            if (sendTime) sendTime.style.display = '';
            if (sendWeekDay) sendWeekDay.style.display = '';
            if (sendMonthDay) sendMonthDay.style.display = 'none';
            if (sendDateTime) sendDateTime.style.display = 'none';
            if (preliminarySwitcher) {
                if ((preliminarySwitcher.children[2].value.toLowerCase() !== 'true')) {
                    preliminarySwitcher.style.display = 'none';
                    preliminaryFieldsShow(false);
                } else preliminaryVisibility();
            }
        break;
        case '3':
            if (sendPeriod) sendPeriod.style.display = 'none';
            if (sendTime) sendTime.style.display = '';
            if (sendWeekDay) sendWeekDay.style.display = 'none';
            if (sendMonthDay) sendMonthDay.style.display = '';
            if (sendDateTime) sendDateTime.style.display = 'none';
            if (preliminarySwitcher) {
                if ((preliminarySwitcher.children[2].value.toLowerCase() !== 'true')) preliminarySwitcher.style.display = '';
                preliminaryVisibility();
            }
        break;
        case '4':
            if (sendPeriod) sendPeriod.style.display = 'none';
            if (sendTime) sendTime.style.display = 'none';
            if (sendWeekDay) sendWeekDay.style.display = 'none';
            if (sendMonthDay) sendMonthDay.style.display = 'none';
            if (sendDateTime) sendDateTime.style.display = '';
            if (preliminarySwitcher) {
                if ((preliminarySwitcher.children[2].value.toLowerCase() !== 'true')) preliminarySwitcher.style.display = '';
                preliminaryVisibility();
            }
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
    var hrefElement = element.parentElement;
    var workerId = hrefElement.getAttribute('title');   
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

function preliminarySwitcherCheckbox(id) {        
    var block = document.querySelector('#' + id);
    let checkBox = block.children[0];
    checkBox.checked = !checkBox.checked;
    setPreliminarySwitcher(checkBox.checked)
    preliminaryVisibility()
}

function preliminaryVisibility() {
    let isShow = document.getElementById('preliminarySwitcher').children[0].checked;
    preliminaryFieldsShow(isShow);
}

function preliminaryFieldsShow(isShow){
    if (isShow) {        
        document.getElementById('preliminarySendTime').style.display = '';
        document.getElementById('preliminarySendBeforeDays').style.display = '';
        document.getElementById('preliminaryText').style.display = '';
    } else {
        document.getElementById('preliminarySendTime').style.display = 'none';
        document.getElementById('preliminarySendBeforeDays').style.display = 'none';
        document.getElementById('preliminaryText').style.display = 'none';
    }
}

function setPreliminarySwitcher(checked) {
    let block = document.getElementById('preliminarySwitcher')
    let textPrem = block.children[1];
    if (checked) {
        textPrem.innerHTML = textPrem.innerHTML.replace('выкл.', 'вкл.');
    } else {
        textPrem.innerHTML = textPrem.innerHTML.replace('вкл.', 'выкл.');
        document.getElementById('preliminarySendBeforeDays').children[1].value = 0;
    }
}