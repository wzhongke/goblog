$(() => {
    initPlugins();
    setTocToggle();
    setAsideToggle();
    loadSvg();
    setSearchBoard();
});

const initPlugins = () => {
    // enable `a` click event inside `li.tab`
    $('.tab').click(function (e) {
        window.location.href = $(this).find('a').prop('href');
    });
    $('.modal').modal();
};

const loadSvg = () => {
    const url = window.origin + '/svg/icon.svg';
    const $area = $('<div style="display:none"></div>').appendTo($('body'));
    $area.load(url);
};

const loadLunrDB = (() => {
    let db = null;
    return () => {
        if (db) return db;
        const url = window.origin + '/index.json';
        db = $.getJSON(url)
            .then(pages => ({
                pageMap: pages
            }))
            .fail((jqxhr, textStatus, error) => {
                console.error(
                    'Error getting Hugo index flie:',
                    textStatus + ', ' + error
                );
                db = null;
            });
        return db;
    };
})();



const setSearchBoard = () => {
    const $in = $('#in-search');
    const $out = $('#out-search');
    const clear = (text = 'No Result...') => {
        const $item = $(
            `<span class="collection-item text-grey" style="display:none">${text}</span>`
        );
        $out.empty().append($item.fadeIn(200));

    };
    clear();
    var queryLength = 0;
    const add = items => {
        if (!items || !items.length) {
            clear();
            return;
        }
        if (!$.isArray(items)) items = [items];
        items = items.slice(0,10);
        const $items = items.map(
            (item, index) =>
                $(`<a href="${item.uri}" class="collection-item nowrap ${index == 0 ? 'selected' : ''}" style="display:none"><i class="material-icons">description</i>${item.title}</a>`)
        );
        $out.empty().append($items);
        queryLength = $items.length;
        $items.forEach($item => $item.fadeIn(100));
    };

    const search = query =>
        loadLunrDB().then(data => {
            add(data.pageMap.filter(value => value.title).filter(value => value.title.includes(query)))
        });

    const keyMap = {};
    keyMap['40'] = 'DOWN';
    keyMap['38'] = 'UP';
    keyMap['13'] = 'ENTER';
    keyMap['16'] = 'SHIFT';
    keyMap['27'] = 'ESCAPE'
    var currentSelect = 0;

    $in.keydown(()=>{
        if (keyMap[event.keyCode] == 'ENTER') {
            var $selected = $out.find('a.selected');
            if ($selected.length > 0) {
                window.location.href = $selected.attr('href');
                event.preventDefault();
                event.stopPropagation();
            }
        }
    })

    $in.keyup(() => {
        const query = $in.val().trim();
        var action = keyMap[event.keyCode];
        if (action) {
            event.stopPropagation();
            event.preventDefault();
            switch (action) {
                case 'UP':
                    $out.find(`a:eq(${currentSelect})`).removeClass('selected');
                    if (currentSelect == 0 && queryLength > 1) {   
                        currentSelect = queryLength - 1;
                        $out.find(`a:eq(${currentSelect})`).addClass('selected');
                    } else if (currentSelect > 0 && queryLength > 1) {
                        currentSelect --;
                        $out.find(`a:eq(${currentSelect})`).addClass('selected');
                    }
                    break;
                case 'DOWN':
                    $out.find(`a:eq(${currentSelect})`).removeClass('selected');
                    if (currentSelect == queryLength - 1 && queryLength > 1) {   
                        currentSelect = 0;
                        $out.find(`a:eq(${currentSelect})`).addClass('selected');
                    } else if (currentSelect >= 0 && queryLength > 1) {
                        currentSelect ++;
                        $out.find(`a:eq(${currentSelect})`).addClass('selected');
                    }
                    break;
                case 'ENTER':
                    var $selected = $out.find('a.selected');
                    if ($selected.length > 0) {
                        window.location.href = $selected.attr('href');
                    }
                    return ;
            }            
            return;
        }

        if (query.length < 1) {
            clear();
            return;
        }
        clear('Searching...');
        search(query);
    });
};
const setTocToggle = () => {
    let onscrollSelect = true;
    const $toc = $('.toc-panel nav');
    const $footer = $('footer.page-footer');
    const $post = $('.post .card');
    const $header = $('nav.navbar');
    const items = [].slice.call($toc.find('li a'));

    let tocHeight = $toc.outerHeight(); // 包括内边距
    let postOffset = $post.offset();
    let headerHeight = $header.height();
    let correction = headerHeight + 20;
    let anchor = getAnchor();
    //function animate above will convert float to int.
    function getAnchor() {
        return items.map(elem =>
            Math.floor($(elem.getAttribute('href')).offset().top - correction)
        );
    }

    function scrolltoElement(elem, cb) {
        var $elem = elem.href ? $(elem.getAttribute('href')) : $(elem);
        $('html, body').animate(
            { scrollTop: $elem.offset().top - correction + 1 },
            400,
            cb
        );
    }

    if ($toc.length === 0) return;

    $toc.on('click', 'a', function (e) {
        e.preventDefault();
        e.stopPropagation();
        onscrollSelect = false;
        $toc.find('a').removeClass('active');
        scrolltoElement(this, () => {
            onscrollSelect = true;
            $(this).addClass('active');
        });
    });

    var scrollListener = () => {
        if (!anchor) return;
        var postHeight = $post.height();
        var scrollTop = $('html').scrollTop() || $('body').scrollTop();
        var isset = false;

        if (scrollTop + headerHeight >= postOffset.top) {
            $toc.removeClass('absolute').addClass('fixed').css('top', headerHeight);
            isset = true;
        }

        if (scrollTop + headerHeight + tocHeight >= postOffset.top + postHeight) {
            $toc
                .removeClass('fixed')
                .addClass('absolute')
                .css({ top: postHeight - tocHeight });
            isset = true;
        }
        if (!isset) {
            $toc.removeClass('fixed').removeClass('absolute').css({ top: 'initial' });
        }
        if (onscrollSelect) {
            //binary search.
            var l = 0,
                r = anchor.length - 1,
                mid;
            while (l < r) {
                mid = (l + r + 1) >> 1;
                if (anchor[mid] === scrollTop) l = r = mid;
                else if (anchor[mid] < scrollTop) l = mid;
                else r = mid - 1;
            }
            $(items).removeClass('active').eq(l).addClass('active');
        }
    };

    $(window).resize(() => {
        anchor = getAnchor();
        postOffset = $post.offset();
        tocHeight = $toc.outerHeight(); // 包括内边距
        headerHeight = $header.height();
        correction = headerHeight + 20;
        onscrollSelect = true;
        scrollListener();
    });

    $(window).scroll(() => scrollListener());

    scrollListener();
};

const setAsideToggle = () => {
    const $aside = $('aside.side-panel');
    const $body = $('body');
    const $swither = $('.button-collapse');
    const $icon = $('i.material-icons', $swither);
    const $cover = $('<div id="js-cover"></div>').appendTo($body);
    const onSwitcherClick = () => {
        if ($aside.hasClass('open')) {
            $aside.removeClass('open');
            $cover.fadeOut(400);
            $body.removeClass('covered');
            $icon.text('menu');
        } else {
            $aside.addClass('open');
            $cover.fadeIn(400);
            $body.addClass('covered');
            $icon.text('close');
        }
    };

    $cover.click(e => {
        e.stopPropagation();
        e.preventDefault();
        onSwitcherClick();
    });

    $swither.click(onSwitcherClick);
};
