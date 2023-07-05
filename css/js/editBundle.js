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
//    console.log(id);
    let delElement = id.parentElement;
//    delElement.style.display = 'none';
//    delElement.id = "delete"
    delElement.parentNode.removeChild(delElement)
}

function addButtonPress() {
    let count = parseInt(document.getElementById('counter').value) + 1;
    document.getElementById('counter').value = count;
    let template = document.getElementById('template');
//    console.log(template)
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
//    console.log(new_element);
    template.after(new_element);
//    console.log(count);
}