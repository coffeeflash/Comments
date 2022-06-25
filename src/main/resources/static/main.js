const baseUrl = location.origin + '/comments/secapi/'

function addComments(source, i){
    let commentsList = $('#category' + i)

    let button = $('#btn' + i)

    button.empty().append('^')
    button.attr('onclick', 'setUp()')

    $.get(baseUrl + 'comments?source=' + source, function(data){

        data.forEach((comment, i) => {
            commentsList.append(
              '<ul><li>' + comment.comment + '</li></ul>'
            )
        })


    })
}

function setUp(){
    $.get(baseUrl + 'commentCategories', function(categories){
        let categoriesUl = $('#categories')
        categoriesUl.empty();
        categories.forEach((category, i) => {
            categoriesUl.append(
                '<li id="category' + i + '">' +
                '<a href="' + makeUrl(category.source) + '">' + category.source + '</a> has ' + category.count +
                ' comments <button id="btn' + i + '" type="button" onclick="addComments(\'' + category.source + '\',\'' + i + '\')" >v</button>'+
                '</li>'
            )
        })
    })
}

function makeUrl(source){
    source = source.replaceAll(" ", "-")
    source = source.toLowerCase()
    return source + ".html"
}

// @ document ready...
$(function(){

    setUp()

    function scrollDown(){
      $("html, body").animate({ scrollTop: $(document).height() }, 1000);
    }

    async function showError(errorText){
      stopWait()
      scrollDown()
      $('#err').empty().append(errorText)
      $('#err').animate({opacity: 1}, 1000 )
      await delay(5000)
      $('#err').animate({opacity: 0}, 5000 )
    }

    function delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    }

    let loadicons = ['|','/','-','\\']
    let i = 0

    async function recursiveWait() {
     $("#btn-submit").prop("disabled",true);
     let loadico = loadicons[i++%4]
     $('#loading').empty().append(loadico + "  Solved " + solvedQuizes + " of " + numToSolve + " hash-quizes")
     loading = true
     await delay(200)
     if(loading) await recursiveWait()
     else i = 0
    }

    function stopWait(){
     $("#btn-submit").prop("disabled",false);
     loading = false
     solvedQuizes = 0
     $('#loading').empty()
    }
})