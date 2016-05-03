/**
 * 
 */
    // var HOST = "http://192.168.1.100:8080";
    var HOST = "http://btmovie.wx.jaeapp.com";
    var user= "nieiqmhj997038@sina.com";
	var currentPage = 1;
	var isHaveNext = true;
    var currentType = "国产电影";
    function nextPage(){
        queryMovie(currentType,1);
    }
    function prevPage(){
        queryMovie(currentType,-1);
    }
	function queryMovie(type,action){
        if(action == 0){
            currentPage = 1;
            isHaveNext = true;
            currentType = type;
            $("#movie_type").html(currentType);
        }
		if(action == 1 && !isHaveNext){
			return;
		}
		if(action == -1 && currentPage == 1){
			return;
		}
        var page;
        if(action == 0){
            page = 1;
        }else if(action == 1){
            page = currentPage + 1;
        }else {
            page = currentPage - 1;
        }
        $.ajax( {
            url: HOST+'/QueryMovie?page='+page+'&pageSize=20&type='+type,
            type: 'GET',
            success: function( response ) {
                // response
                var movies = response.body;
                var grids_html="";
				var len = movies.length;
				if(len < 20){
					isHaveNext = false;
				}
				if(action == 1 && isHaveNext){
					currentPage++;
				}
				if(action == -1 && currentPage > 1){
					currentPage--;
				}
                for(var i = 0; i< len ;i++){
                    var object = movies[i];
                    var grid_html="";
                    if((i+1)%3==0){
                        grid_html+="<div class='content-grid last-grid'>";
                    }else{
                        grid_html+="<div class='content-grid'>";
                    }
					var name = object.name;
					var re = /\.\w+/;
					name = name.split(re)[0];
                    if(name.length > 14){
                       name = name.substring(0,12)+"...";
                    }
                    grid_html+="<a href='./play.html?movidid="+object.objectId+"&moviename="+name+"' name='"+name+" target='_blank' ><img class='movie-image' src='"+object.mainImageUrl+"' title="+name+" /></a>";
                    grid_html+="<p class='moviename'><span>"+name+"</span></p>";
                    //grid_html+="<ul><li><a href='#'><img src='./images/likes.png' title='image-name' /></a></li>";
                    //grid_html+="<li><a href='#'><img src='./images/views.png' title='image-name' /></a></li>";
                    //grid_html+="<li><a href='#'><img src='./images/link.png' title='image-name' /></a></li></ul>";
                    grid_html+="<a class='button' href='./play.html?movidid="+object.objectId+"&moviename="+name+"' target='_blank'>点击观看</a></div>";
                    grids_html+=grid_html;
                }
                grids_html+="<div class='clear'> </div>";
                $("#content_grids").html(grids_html);
            }
        } );

	}
    function showMovieInfo(movieid){
        //movieId
        $.ajax( {
            url: HOST+'/QueryMovieInfo?movieId='+movieid,
            type: 'GET',
            success: function( response ) {
                // response
                var movieInfo = response.body;
                $("#movie_info").html(movieInfo.info);
            }
        } );
    }
$.request = (function () {
    var apiMap = {};
    function request(queryStr) {
        var api = {};
        if (apiMap[queryStr]) { return apiMap[queryStr]; }
        api.queryString = (function () {
            var urlParams = {};
            var e,
                d = function (s) { return decodeURIComponent(s.replace(/\+/g, " ")); },
                q = queryStr.substring(queryStr.indexOf('?') + 1),
                r = /([^&=]+)=?([^&]*)/g;
            while (e = r.exec(q))   urlParams[d(e[1])] = d(e[2]);
            return urlParams;
        })();
        api.getUrl = function () {
            var url = queryStr.substring(0, queryStr.indexOf('?') + 1);
            for (var p in api.queryString) { url += p + '=' + api.queryString[p] + "&";     }
            if (url.lastIndexOf('&') == url.length - 1) { return url.substring(0, url.lastIndexOf('&')); }
            return url;
        }
        apiMap[queryStr] = api;
        return api;
    }
    $.extend(request, request(window.location.href));
    return request;
})();