// to differ dev / prod env
const baseUrl = (location.origin.includes("4000") ? 'http://localhost:8080' : location.origin) + '/comments'

const commentSource = document.URL.replace(/#[^#]*$/, "").replace(/\?[^\?]*$/, "")

let loading = false
let solvedQuizes = 0
let numToSolve = 0

setUp()

async function setUp(){

    const response = await fetch(baseUrl + '/api/comments?source=' + commentSource)
    const comments = await response.json()


    const commentsSecEl =  document.getElementById('comment-section')
    commentsSecEl.style.opacity = 1
    commentsSecEl.innerHTML =
        '<h2>Comment - Section</h2>'+
        '<p>No subscription to any service needed! Instead your device has to solve '+
        'some hash quizes to successfully submit a comment.</p>'+
        '<div id="comments"></div>'+
        '<div id="addComment">'+
        '  <button id="x" type="button" onclick="addComment()"> Add a comment </button>'+
        '</div>'

    const commentsDiv = document.getElementById('comments')

    commentsDiv.innerHtml = ""
    commentsDiv.style.opacity = '0'

    comments.forEach(comment => {
        commentsDiv.insertAdjacentHTML('beforeend',
            '<div style="margin-top: 0.6rem;\n' +
            '  margin-bottom: 0.6rem;' +
            '  padding: 0.5rem;' +
            '  background-color: lightgray;' +
            '  color: black;' +
            '  border-radius: 5px;' +
            '   margin-right: 6%;">' +
            '<h3 style="margin: 0;">' + comment.user +
            '<span style="color:gray;font-size:0.8rem;"> (' + new Date(comment.date).toLocaleString() + ')</span>'+
            '</h3>'+
            '<p style="margin:0;">' + comment.comment + '</p></div>')

        if ( comment.reply ){
            commentsDiv.insertAdjacentHTML('beforeend',
                '<div style="  margin-top: 0.6rem;\n' +
                '  margin-bottom: 0.6rem;\n' +
                '  padding: 0.5rem;\n' +
                '  background-color: lightgray;\n' +
                '  color: black;\n' +
                '  border-radius: 5px;' +
                '  color: #FF4500;\n' +
                '  margin-left: 6%;"><h3 style="margin: 0;">' + comment.admin +
                '<span style="color:rgb(128,128,128);font-size:0.8rem;"> (' + new Date(comment.replyDate).toLocaleString() + ')</span></h3>'+
                '<p  style="margin:0;">' + comment.reply + '</p></div>')
        }

    })

    fadeIn(commentsDiv)
}

function fadeIn (obj, callback, duration = 50) {
    obj.style.display = '';
    let op = 0;
    const timer = setInterval(function () {
        if (op >= 1 || op >= 1.0){
            clearInterval(timer);
            if(typeof (callback) !== 'undefined' && callback!=null) callback()
        }
        obj.style.opacity = op.toFixed(1);
        op += 0.1;
    }, duration);

}

function fadeOut(obj, callback, duration = 50){
    let opacity = 1;
    const timer = setInterval(function(){
        if(opacity < 0.1){
            clearInterval(timer);
            if(typeof (callback) !== 'undefined' && callback!=null) callback() //this executes the callback function!
        }
        obj.style.opacity = opacity;
        opacity -=  0.1;
    }, duration);
}

let slideUp = (target, callback, duration=1000) => {
    target.style.transitionProperty = 'height, margin, padding';
    target.style.transitionDuration = duration + 'ms';
    target.style.boxSizing = 'border-box';
    target.style.height = target.offsetHeight + 'px';
    target.offsetHeight;
    target.style.overflow = 'hidden';
    target.style.height = 0;
    target.style.paddingTop = 0;
    target.style.paddingBottom = 0;
    target.style.marginTop = 0;
    target.style.marginBottom = 0;
    window.setTimeout( () => {
        target.style.display = 'none';
        target.style.removeProperty('height');
        target.style.removeProperty('padding-top');
        target.style.removeProperty('padding-bottom');
        target.style.removeProperty('margin-top');
        target.style.removeProperty('margin-bottom');
        target.style.removeProperty('overflow');
        target.style.removeProperty('transition-duration');
        target.style.removeProperty('transition-property');
        //alert("!");
        if(typeof (callback) !== 'undefined' && callback!=null) callback()
    }, duration);
}

let slideDown = (target, duration=500) => {
    target.style.removeProperty('display');
    let display = window.getComputedStyle(target).display;

    if (display === 'none')
        display = 'block';

    target.style.display = display;
    let height = target.offsetHeight;
    target.style.overflow = 'hidden';
    target.style.height = 0;
    target.style.paddingTop = 0;
    target.style.paddingBottom = 0;
    target.style.marginTop = 0;
    target.style.marginBottom = 0;
    target.offsetHeight;
    target.style.boxSizing = 'border-box';
    target.style.transitionProperty = "height, margin, padding";
    target.style.transitionDuration = duration + 'ms';
    target.style.height = height + 'px';
    target.style.removeProperty('padding-top');
    target.style.removeProperty('padding-bottom');
    target.style.removeProperty('margin-top');
    target.style.removeProperty('margin-bottom');
    window.setTimeout( () => {
        target.style.removeProperty('height');
        target.style.removeProperty('overflow');
        target.style.removeProperty('transition-duration');
        target.style.removeProperty('transition-property');
    }, duration);
}

function bytesToHexString (bytes){
    return bytes.map(b => b.toString(16).padStart(2, '0')).join('')           // convert bytes to hex string
}

async function recursiveHash (msgUint8) {
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgUint8)        // hash the message
    const hashArray = Array.from(new Uint8Array(hashBuffer))                  // convert buffer to byte array
    const hashHex = bytesToHexString(hashArray)
    return hashHex;
}

function checkNBytes(digestHex, numZeroBytes){
    for (let i = 0; i < numZeroBytes*2; i++) {
        if (digestHex[i] != 0) return true;                                     // search further hashes...
    }
    return false;
}

async function searchHash(quizContent, securityLevel){
    let digestHex = ""
    let nonce = 0
    do{
        nonce++
        let nonceAndMsg = new TextEncoder().encode(nonce + quizContent)
        digestHex = await recursiveHash(nonceAndMsg);
        if(!loading) return // to ensure to stop the loop, when reply is canceled
    }while (checkNBytes(digestHex, securityLevel))
    return nonce
}

function scrollDown(){
    window.scrollTo({
        top: document.body.scrollHeight, left: 0,
        behavior: 'smooth'});
}

async function addComment(){
    const addComment = document.getElementById("addComment")
    document.getElementById("x").remove()
    addComment.style.opacity = '0'
    addComment.insertAdjacentHTML( 'beforeend',
        '<h1>Add your comment:</h1>'+
        '<p style="color:rgb(128,128,128);" id="loading"></p>'+
        '<label for="name">Name:</label><br>'+
        '<input id="name" name="name" placeholder="Your name"'+
        'style="width: 100%; max-width: 200px; border-radius: 5px; border-style: solid; border-color: lightgray;"><br>'+
        '<label for="text-comment">Comment:</label><br>'+
        '<textarea id="text-comment" name="text-comment" placeholder="Write something..."'+
        ' style="width: 100%;min-height: 6rem; border-radius: 5px; border-style: solid; border-color: lightgray;">'+
        '</textarea><br><br>'+
        '<button id="btn-submit" type="button" onclick="sendComment()">Send</button>'+
        '<button type="button" style="margin-left: 1rem;" onclick="cancelReply()">Cancel</button>'+
        '<p style="color:red;opacity:0;" id="err"></p>')
    fadeIn(addComment)
    scrollDown()
}

async function cancelReply(){
    const addComment = document.getElementById("addComment")
    loading = false
    slideUp(addComment, function() {
        addComment.innerHTML = '<button id="x" type="button" onclick="addComment()"> Add a comment </button>'
        addComment.style.opacity = '0'
        fadeIn(addComment,null, 20)
    }, 200)
}

async function showError(errorText){
    stopWait()
    const error = document.getElementById('err')
    error.innerHTML = errorText
    scrollDown()
    fadeIn(error, async function () {
        await delay(5000)
        fadeOut(error)
    })
}

async function sendComment(){
    const validRegex = /^(\d|;|\?|\(|\)|\[|\]|!|-|’|'|:|,|\.|\s|[\u0041-\u005A\u0061-\u007A\u00AA\u00B5\u00BA\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02C1\u02C6-\u02D1\u02E0-\u02E4\u02EC\u02EE\u0370-\u0374\u0376\u0377\u037A-\u037D\u0386\u0388-\u038A\u038C\u038E-\u03A1\u03A3-\u03F5\u03F7-\u0481\u048A-\u0527\u0531-\u0556\u0559\u0561-\u0587\u05D0-\u05EA\u05F0-\u05F2\u0620-\u064A\u066E\u066F\u0671-\u06D3\u06D5\u06E5\u06E6\u06EE\u06EF\u06FA-\u06FC\u06FF\u0710\u0712-\u072F\u074D-\u07A5\u07B1\u07CA-\u07EA\u07F4\u07F5\u07FA\u0800-\u0815\u081A\u0824\u0828\u0840-\u0858\u08A0\u08A2-\u08AC\u0904-\u0939\u093D\u0950\u0958-\u0961\u0971-\u0977\u0979-\u097F\u0985-\u098C\u098F\u0990\u0993-\u09A8\u09AA-\u09B0\u09B2\u09B6-\u09B9\u09BD\u09CE\u09DC\u09DD\u09DF-\u09E1\u09F0\u09F1\u0A05-\u0A0A\u0A0F\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32\u0A33\u0A35\u0A36\u0A38\u0A39\u0A59-\u0A5C\u0A5E\u0A72-\u0A74\u0A85-\u0A8D\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0\u0AB2\u0AB3\u0AB5-\u0AB9\u0ABD\u0AD0\u0AE0\u0AE1\u0B05-\u0B0C\u0B0F\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32\u0B33\u0B35-\u0B39\u0B3D\u0B5C\u0B5D\u0B5F-\u0B61\u0B71\u0B83\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99\u0B9A\u0B9C\u0B9E\u0B9F\u0BA3\u0BA4\u0BA8-\u0BAA\u0BAE-\u0BB9\u0BD0\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C33\u0C35-\u0C39\u0C3D\u0C58\u0C59\u0C60\u0C61\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CBD\u0CDE\u0CE0\u0CE1\u0CF1\u0CF2\u0D05-\u0D0C\u0D0E-\u0D10\u0D12-\u0D3A\u0D3D\u0D4E\u0D60\u0D61\u0D7A-\u0D7F\u0D85-\u0D96\u0D9A-\u0DB1\u0DB3-\u0DBB\u0DBD\u0DC0-\u0DC6\u0E01-\u0E30\u0E32\u0E33\u0E40-\u0E46\u0E81\u0E82\u0E84\u0E87\u0E88\u0E8A\u0E8D\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EA5\u0EA7\u0EAA\u0EAB\u0EAD-\u0EB0\u0EB2\u0EB3\u0EBD\u0EC0-\u0EC4\u0EC6\u0EDC-\u0EDF\u0F00\u0F40-\u0F47\u0F49-\u0F6C\u0F88-\u0F8C\u1000-\u102A\u103F\u1050-\u1055\u105A-\u105D\u1061\u1065\u1066\u106E-\u1070\u1075-\u1081\u108E\u10A0-\u10C5\u10C7\u10CD\u10D0-\u10FA\u10FC-\u1248\u124A-\u124D\u1250-\u1256\u1258\u125A-\u125D\u1260-\u1288\u128A-\u128D\u1290-\u12B0\u12B2-\u12B5\u12B8-\u12BE\u12C0\u12C2-\u12C5\u12C8-\u12D6\u12D8-\u1310\u1312-\u1315\u1318-\u135A\u1380-\u138F\u13A0-\u13F4\u1401-\u166C\u166F-\u167F\u1681-\u169A\u16A0-\u16EA\u1700-\u170C\u170E-\u1711\u1720-\u1731\u1740-\u1751\u1760-\u176C\u176E-\u1770\u1780-\u17B3\u17D7\u17DC\u1820-\u1877\u1880-\u18A8\u18AA\u18B0-\u18F5\u1900-\u191C\u1950-\u196D\u1970-\u1974\u1980-\u19AB\u19C1-\u19C7\u1A00-\u1A16\u1A20-\u1A54\u1AA7\u1B05-\u1B33\u1B45-\u1B4B\u1B83-\u1BA0\u1BAE\u1BAF\u1BBA-\u1BE5\u1C00-\u1C23\u1C4D-\u1C4F\u1C5A-\u1C7D\u1CE9-\u1CEC\u1CEE-\u1CF1\u1CF5\u1CF6\u1D00-\u1DBF\u1E00-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F59\u1F5B\u1F5D\u1F5F-\u1F7D\u1F80-\u1FB4\u1FB6-\u1FBC\u1FBE\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC\u2071\u207F\u2090-\u209C\u2102\u2107\u210A-\u2113\u2115\u2119-\u211D\u2124\u2126\u2128\u212A-\u212D\u212F-\u2139\u213C-\u213F\u2145-\u2149\u214E\u2183\u2184\u2C00-\u2C2E\u2C30-\u2C5E\u2C60-\u2CE4\u2CEB-\u2CEE\u2CF2\u2CF3\u2D00-\u2D25\u2D27\u2D2D\u2D30-\u2D67\u2D6F\u2D80-\u2D96\u2DA0-\u2DA6\u2DA8-\u2DAE\u2DB0-\u2DB6\u2DB8-\u2DBE\u2DC0-\u2DC6\u2DC8-\u2DCE\u2DD0-\u2DD6\u2DD8-\u2DDE\u2E2F\u3005\u3006\u3031-\u3035\u303B\u303C\u3041-\u3096\u309D-\u309F\u30A1-\u30FA\u30FC-\u30FF\u3105-\u312D\u3131-\u318E\u31A0-\u31BA\u31F0-\u31FF\u3400-\u4DB5\u4E00-\u9FCC\uA000-\uA48C\uA4D0-\uA4FD\uA500-\uA60C\uA610-\uA61F\uA62A\uA62B\uA640-\uA66E\uA67F-\uA697\uA6A0-\uA6E5\uA717-\uA71F\uA722-\uA788\uA78B-\uA78E\uA790-\uA793\uA7A0-\uA7AA\uA7F8-\uA801\uA803-\uA805\uA807-\uA80A\uA80C-\uA822\uA840-\uA873\uA882-\uA8B3\uA8F2-\uA8F7\uA8FB\uA90A-\uA925\uA930-\uA946\uA960-\uA97C\uA984-\uA9B2\uA9CF\uAA00-\uAA28\uAA40-\uAA42\uAA44-\uAA4B\uAA60-\uAA76\uAA7A\uAA80-\uAAAF\uAAB1\uAAB5\uAAB6\uAAB9-\uAABD\uAAC0\uAAC2\uAADB-\uAADD\uAAE0-\uAAEA\uAAF2-\uAAF4\uAB01-\uAB06\uAB09-\uAB0E\uAB11-\uAB16\uAB20-\uAB26\uAB28-\uAB2E\uABC0-\uABE2\uAC00-\uD7A3\uD7B0-\uD7C6\uD7CB-\uD7FB\uF900-\uFA6D\uFA70-\uFAD9\uFB00-\uFB06\uFB13-\uFB17\uFB1D\uFB1F-\uFB28\uFB2A-\uFB36\uFB38-\uFB3C\uFB3E\uFB40\uFB41\uFB43\uFB44\uFB46-\uFBB1\uFBD3-\uFD3D\uFD50-\uFD8F\uFD92-\uFDC7\uFDF0-\uFDFB\uFE70-\uFE74\uFE76-\uFEFC\uFF21-\uFF3A\uFF41-\uFF5A\uFF66-\uFFBE\uFFC2-\uFFC7\uFFCA-\uFFCF\uFFD2-\uFFD7\uFFDA-\uFFDC])+$/;
    const validationErrText = 'Please enter a valid name and a comment.<br>'+
        'The only allowed special characters are: "(", ")" ,"[" ,"]" ,";" ,",", "!", "?", ":", ".", "-"'
    if(!validRegex.test(document.getElementById('text-comment').value) || !validRegex.test(document.getElementById('name').value)){
        showError(validationErrText)
        return
    }

    const quizResponse = await fetch(baseUrl + '/api/quiz')
    const quiz = await quizResponse.json()
    numToSolve = quiz.contents.length
    solvedQuizes = 0
    const loadingEl = document.getElementById('loading')
    loadingEl.style.opacity = 0
    // slideDown(loadingEl, 100)
    fadeIn(loadingEl,null, 20)
    recursiveWait() // only loading animation...
    let validNonces = []
    for (let i = 0; i < quiz.contents.length; i++) {
        let validNonce = await searchHash(quiz.contents[i], quiz.securityLevel)
        solvedQuizes++
        validNonces.push(validNonce)
        if (!loading) return
    }

    const response = await fetch(baseUrl + '/api/comment', {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            sourceTitle: commentSource,
            source: commentSource,
            user: document.getElementById('name').value,
            comment: document.getElementById('text-comment').value,
            quizId: quiz.contents[0],
            quizSolutions: validNonces
        })
    })

    const message = await response.text()
    if(message != "ok"){
        showError(message)
        return
    }

    await delay(700)
    stopWait()
    fadeOut(document.getElementById('comment-section'),async function () {
        await setUp()
        numToSolve = 0
    }, 25)
}

function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

const loadicons = ['|','/','-','\\']
let i = 0

async function recursiveWait() {
    const submitBtn = document.getElementById('btn-submit')
    if(submitBtn != null) submitBtn.disabled = true
    let loadico = loadicons[i++%4]
    const loadingEle =  document.getElementById('loading')
    if(loadingEle != null) loadingEle.innerHTML = loadico + "  Solved " + solvedQuizes
        + " of " + numToSolve + " hash-quizes"
    loading = true
    await delay(200)
    if(loading) await recursiveWait()
    else i = 0
}

function stopWait(){
    document.getElementById('btn-submit').disabled = false
    loading = false
    document.getElementById('loading').innerHTML = ''
}
