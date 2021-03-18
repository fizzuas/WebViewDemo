(function() {
    var toolPara = {};
    toolPara.select_count = 0;
    window.appCurPageNum = 1;
    function initTools() {
        let totalPages = window.totalPages || $(".results").first().attr("data-totalPages") || 10;
        if (totalPages > 1) {
            $("#next_page").show()
        }
        var toolStr = localStorage.getItem('weixin_high_select_' + encodeURIComponent(window.oldQuery));
        if (toolStr !== null) {
            let toolParaObj = JSON.parse(toolStr);
            if (toolParaObj.tsn) {
                $(".conditionTime").each(function() {
                    if ($(this).attr("data-type") === toolParaObj.tsn) {
                        $(this).addClass('selected')
                    }
                })
            }
            if (toolParaObj.interation) {
                $(".outerSelect").each(function() {
                    if (toolParaObj.interation.includes($(this).attr("data-type"))) {
                        $(this).addClass('active')
                    }
                })
            }
            if (toolParaObj.usip) {
                $('#select_in_account').val(toolParaObj.usip)
            }
            if (toolParaObj.select_count > 0) {
                toolPara.select_count = toolParaObj.select_count;
                $('#select_start').addClass('active')
            }
        }
    }
    function objToUrlParam(obj) {
        var arr = [];
        for (var name in obj) {
            arr.push("&", name, "=", encodeURIComponent(obj[name]))
        }
        return arr.join("")
    }
    function buildToolUrl(paras) {
        return "type=2&ie=utf8&page=" + window.appCurPageNum + "&query=" + encodeURIComponent(window.oldQuery) + objToUrlParam(paras)
    }
    function successBack(data) {
        return data && (data.code === "success" || data.code === "ok" || data.code === "exist")
    }
    function isSousuoApp() {
        if (window.JSInvoker && (typeof window.JSInvoker.eWallGetWithResponse !== "undefined")) {
            return true
        } else {
            return false
        }
    }
    function loading() {
        if (window.appCurPageNum === 1) {
            $(".more-num").remove();
            $(".results").first().empty().append("<div class=\"mod-loading\">\n" + "\t\t\t\t<span class=\"icon\"></span>\n" + "\t\t\t\t<p class=\"tip\">正在加载内容中</p>\n" + "\t\t\t</div>");
            $('.filter-panel').toggle();
            $('.filter-mask').toggle()
        }
    }
    function loading2() {
        if (window.appCurPageNum === 1) {
            $(".more-num").remove();
            $(".results").first().empty().append("<div class=\"mod-loading\">\n" + "\t\t\t\t<span class=\"icon\"></span>\n" + "\t\t\t\t<p class=\"tip\">正在加载内容中</p>\n" + "\t\t\t</div>")
        }
    }
    function finish() {
        if (window.appCurPageNum === 1) {
            $(".mod-loading").hide()
        }
    }
    function sendPara4SousuoApp(param) {
        loading();
        var params = buildToolUrl(param);
        var targetUrl = "http://app.weixin.sogou.com/api/searchapp";
        window.JSInvoker.eWallGetWithResponse(targetUrl, params, "", "callback4app", 3000)
    }
    function sendPara4SousuoApp2(param) {
        loading2();
        var params = buildToolUrl(param);
        var targetUrl = "http://app.weixin.sogou.com/api/searchapp";
        window.JSInvoker.eWallGetWithResponse(targetUrl, params, "", "callback4app", 3000)
    }
    window.callback4app = function(status, response) {
        finish();
        if (status === 1 && response && response.length === 106 || status === 0) {
            $(".results,#next_page,.account-txt").remove();
            $(".tab-top").after("<div class=\"results\" data-page=\"1\">\n" + "    <div class=\"vrResult\"><div class=\"img-err\">\n" + "    <p><img src=\"/new/wap/images/img_err.png\" width=\"140\"></p>\n" + "    <p>抱歉，没有找到相关的微信文章。</p>\n" + "</div></div><div class=\"vrResult\">\n" + "            <iframe scrolling=\"no\" frameborder=\"0\" src=\"https://yibo.iyiyun.com/Home/Distribute/ad404/key/17381\" width=\"100%\" height=\"200\" style=\"display:block;\"></iframe>\n" + "        </div></div>")
        } else if (status === 1 && response && response.length > 135) {
            if (window.appCurPageNum === 1) {
                $(".results,.more-num").remove();
                $(".tab-top").after(response)
            } else {
                $(".results").last().after(response)
            }
            if ($(response).find('li').length === 10) {
                uigs_para.s_from = "nextpage";
                uigs_para.page = window.appCurPageNum;
                uigs_pv()
            } else {
                $("#next_page").hide()
            }
        } else {
            $("#next_page").hide()
        }
    }
    ;
    $("#select_start,.filter-mask").on("click", function() {
        $('.filter-panel').toggle();
        $('.filter-mask').toggle()
    });
    $("#query").focus(function() {
        $('.filter-panel').hide();
        $('.filter-mask').hide()
    });
    $("#resetbtn").on("click", function() {
        $('.filter-panel').hide();
        $('.filter-mask').hide()
    });
    $(".conditionTime").on("click", function() {
        var count_befero = 0
          , count_after = 0;
        $(".conditionTime").each(function(value) {
            if ($(this).hasClass('selected')) {
                count_befero++
            }
        });
        $(this).toggleClass('selected');
        $(".conditionTime").not($(this)).removeClass('selected');
        $(".conditionTime").each(function(value) {
            if ($(this).hasClass('selected')) {
                count_after++
            }
        });
        if (count_befero > count_after) {
            toolPara.select_count--
        } else if (count_befero < count_after) {
            toolPara.select_count++
        }
        if (toolPara.select_count) {
            $('#select_start').addClass('active')
        } else {
            $('#select_start').removeClass('active')
        }
    });
    $("#select_in_account").change(function() {
        if (!!$(this).val().trim()) {
            toolPara.select_count++
        } else {
            toolPara.select_count--
        }
        if (toolPara.select_count) {
            $('#select_start').addClass('active')
        } else {
            $('#select_start').removeClass('active')
        }
    });
    $("#select_clear,#resetbtn,.btn-search").on("click", function() {
        $(".conditionTime").removeClass('selected');
        $('#select_start').removeClass('active');
        $("#select_in_account").val('');
        localStorage.removeItem('weixin_high_select_' + encodeURIComponent(window.oldQuery))
    });
    $("body").on("click", ".list-txt>h4>a,.list-txt>a,.pic>a,.tab-top>a", function() {
        $('.outerSelect').removeClass('active');
        $('#select_start').removeClass('active');
        $(".conditionTime").removeClass('selected');
        $("#select_in_account").val('');
        localStorage.removeItem('weixin_high_select_' + encodeURIComponent(window.oldQuery))
    });
    $(".outerSelect").on("click", function() {
        if (isSousuoApp()) {
            if (!$(this).hasClass('active')) {
                if ($(this).is("#select_pics")) {
                    uigs_cl('select_pics_confirm')
                } else if ($(this).is("#select_videos")) {
                    uigs_cl('select_videos_confirm')
                }
            }
            $(this).toggleClass('active');
            $("#show_login_info").hide();
            $("#next_page").show();
            window.appCurPageNum = 1;
            var outerSelect = [];
            $(".outerSelect").each(function() {
                if ($(this).hasClass('active')) {
                    outerSelect.push($(this).attr("data-type"))
                }
            });
            delete toolPara.interation;
            toolPara.interation = outerSelect.join(",");
            localStorage.setItem('weixin_high_select_' + encodeURIComponent(window.oldQuery), JSON.stringify(toolPara));
            sendPara4SousuoApp2(toolPara)
        } else {
            alert("you are not supported by us")
        }
    });
    $("#select_confirm,#next_page").on("click", function() {
        if (isSousuoApp()) {
            if ($(this).is("#next_page")) {
                window.appCurPageNum++;
                if (window.appCurPageNum >= 10) {
                    if (!window.isLogin) {
                        $("#show_login_info").show();
                        $(this).hide()
                    }
                }
            } else {
                $("#show_login_info").hide();
                $("#next_page").show();
                window.appCurPageNum = 1
            }
            delete toolPara.tsn;
            delete toolPara.wxid;
            delete toolPara.wxid;
            delete toolPara.usip;
            $(".conditionTime").each(function() {
                if ($(this).hasClass('selected')) {
                    toolPara.tsn = $(this).attr("data-type");
                    return false
                }
            });
            var userInput = $('#select_in_account').val().trim();
            toolPara.usip = userInput;
            localStorage.setItem('weixin_high_select_' + encodeURIComponent(window.oldQuery), JSON.stringify(toolPara));
            if (!!userInput) {
                $.ajax({
                    url: "weixin?zhnss=1&type=1&ie=utf8&query=" + encodeURIComponent(userInput),
                    dataType: "json",
                    success: function(data) {
                        if (successBack(data) && data.openid && (userInput === data.weixinname || userInput === data.weixinhao)) {
                            toolPara.wxid = data.openid
                        } else {
                            toolPara.wxid = 'helloworld'
                        }
                        sendPara4SousuoApp(toolPara)
                    },
                    error: function() {
                        toolPara.wxid = 'helloworld';
                        sendPara4SousuoApp(toolPara)
                    }
                })
            } else {
                sendPara4SousuoApp(toolPara)
            }
        } else {
            alert("you are not supported by us")
        }
    });
    $(document).ready(function() {
        uigs_pv('type=weixin_sousuo_app');
        $("#select_pics").on("click", function() {
            uigs_cl('select_pics')
        });
        $("#select_videos").on("click", function() {
            uigs_cl('select_videos')
        });
        $("#select_start").on("click", function() {
            uigs_cl('select_start')
        });
        $("#select_confirm").on("click", function() {
            uigs_cl('select_confirm')
        })
    });
    initTools()
}
)();
