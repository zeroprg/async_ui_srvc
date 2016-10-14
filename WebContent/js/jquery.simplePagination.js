/**
* simplePagination.js v1.6
* A simple jQuery pagination plugin.
* http://flaviusmatis.github.com/simplePagination.js/
*
* Copyright 2012, Flavius Matis
* Released under the MIT license.
* http://flaviusmatis.github.com/license.html
*/


function initPagination(tableId,rowsPerPage) {
    var items = $("#" + tableId +" tbody tr");
    var numItems = items.length;
    var perPage = rowsPerPage;
    // only show the first 2 (or "first per_page") items initially
    items.slice(perPage).hide();

    // now setup pagination
    $("#pagination").pagination({
        items: numItems,
        itemsOnPage: perPage,
        cssStyle: "light-theme",
        onPageClick: function(pageNumber) { // this is where the magic happens
            // someone changed page, lets hide/show trs appropriately
            var showFrom = perPage * (pageNumber - 1);
            var showTo = showFrom + perPage;

            items.hide() // first hide everything, then show for the new page
                 .slice(showFrom, showTo).show();
            // define callback function to my code
            callBackPageSelected(pageNumber);
        }
    });
    /*  Use this function if you want create parameter hash #page-1 (1,2,3,..n) */

    
    // next we'll create a function to check the url fragment and change page if necessary
    // we're storing this function in a variable so we can reuse it
    var checkFragment = function() {
        // if there's no hash, make sure we go to page 1
        var hash = window.location.hash || "#page-1";

        // we'll use regex to check the hash string as follows:
        // ^            strictly from the beginning of the string (i.e. succeed "#page-3" but fail "hi!#page-3")
        // #page-       exactly match the text "#page-"
        // (            start a matching group (so we can access what's in these parentheses on their own)
        //      \d      any digit ([0-9])
        //      +       one or more of the previous literal (one or more digits)
        // )            end the matching group
        // $            we should now be at the end of the string - if not, then don't match (i.e. fail "#page-3hi!")
        hash = hash.match(/^#page-(\d+)$/);

        if(hash)
            // the selectPage function is one of many described in the documentation
            // we've captured the page number in a regex group: (\d+)
            $("#pagination").pagination("selectPage", parseInt(hash[1]));
    };

    // we'll call this function whenever the back or forward buttons are pressed
    // thanks to mike o'connor for highlighting the need for this
    $(window).bind("popstate", checkFragment);
    // and we'll also call it to check right now!
    checkFragment();
}


(function($){

	var methods = {
		init: function(options) {
			var o = $.extend({
				items: 1,
				itemsOnPage: 1,
				pages: 0,
				displayedPages: 5,
				edges: 2,
				currentPage: 1,
				hrefTextPrefix: '#page-',
				hrefTextSuffix: '',
				prevText: 'Prev',
				nextText: 'Next',
				ellipseText: '&hellip;',
				cssStyle: 'light-theme',
				labelMap: [],
				selectOnClick: true,
				onPageClick: function(pageNumber, event) {
					// Callback triggered when a page is clicked
					// Page number is given as an optional parameter
				},
				onInit: function() {
					// Callback triggered immediately after initialization
				}
			}, options || {});

			var self = this;

			o.pages = o.pages ? o.pages : Math.ceil(o.items / o.itemsOnPage) ? Math.ceil(o.items / o.itemsOnPage) : 1;
			o.currentPage = o.currentPage - 1;
			o.halfDisplayed = o.displayedPages / 2;

			this.each(function() {
				self.addClass(o.cssStyle + ' simple-pagination').data('pagination', o);
				methods._draw.call(self);
			});

			o.onInit();

			return this;
		},

		selectPage: function(page) {
			methods._selectPage.call(this, page - 1);
			return this;
		},

		prevPage: function() {
			var o = this.data('pagination');
			if (o.currentPage > 0) {
				methods._selectPage.call(this, o.currentPage - 1);
			}
			return this;
		},

		nextPage: function() {
			var o = this.data('pagination');
			if (o.currentPage < o.pages - 1) {
				methods._selectPage.call(this, o.currentPage + 1);
			}
			return this;
		},

		getPagesCount: function() {
			return this.data('pagination').pages;
		},

		getCurrentPage: function () {
			return this.data('pagination').currentPage + 1;
		},

		destroy: function(){
			this.empty();
			return this;
		},

		drawPage: function (page) {
			var o = this.data('pagination');
			o.currentPage = page - 1;
			this.data('pagination', o);
			methods._draw.call(this);
			return this;
		},

		redraw: function(){
			methods._draw.call(this);
			return this;
		},

		disable: function(){
			var o = this.data('pagination');
			o.disabled = true;
			this.data('pagination', o);
			methods._draw.call(this);
			return this;
		},

		enable: function(){
			var o = this.data('pagination');
			o.disabled = false;
			this.data('pagination', o);
			methods._draw.call(this);
			return this;
		},

		updateItems: function (newItems) {
			var o = this.data('pagination');
			o.items = newItems;
			o.pages = methods._getPages(o);
			this.data('pagination', o);
			methods._draw.call(this);
		},

		updateItemsOnPage: function (itemsOnPage) {
			var o = this.data('pagination');
			o.itemsOnPage = itemsOnPage;
			o.pages = methods._getPages(o);
			this.data('pagination', o);
			methods._selectPage.call(this, 0);
			return this;
		},

		_draw: function() {
			var	o = this.data('pagination'),
				interval = methods._getInterval(o),
				i,
				tagName;

			methods.destroy.call(this);
			
			tagName = (typeof this.prop === 'function') ? this.prop('tagName') : this.attr('tagName');

			var $panel = tagName === 'UL' ? this : $('<ul></ul>').appendTo(this);

			// Generate Prev link
			if (o.prevText) {
				methods._appendItem.call(this, o.currentPage - 1, {text: o.prevText, classes: 'prev'});
			}

			// Generate start edges
			if (interval.start > 0 && o.edges > 0) {
				var end = Math.min(o.edges, interval.start);
				for (i = 0; i < end; i++) {
					methods._appendItem.call(this, i);
				}
				if (o.edges < interval.start && (interval.start - o.edges != 1)) {
					$panel.append('<li class="disabled"><span class="ellipse">' + o.ellipseText + '</span></li>');
				} else if (interval.start - o.edges == 1) {
					methods._appendItem.call(this, o.edges);
				}
			}

			// Generate interval links
			for (i = interval.start; i < interval.end; i++) {
				methods._appendItem.call(this, i);
			}

			// Generate end edges
			if (interval.end < o.pages && o.edges > 0) {
				if (o.pages - o.edges > interval.end && (o.pages - o.edges - interval.end != 1)) {
					$panel.append('<li class="disabled"><span class="ellipse">' + o.ellipseText + '</span></li>');
				} else if (o.pages - o.edges - interval.end == 1) {
					methods._appendItem.call(this, interval.end++);
				}
				var begin = Math.max(o.pages - o.edges, interval.end);
				for (i = begin; i < o.pages; i++) {
					methods._appendItem.call(this, i);
				}
			}

			// Generate Next link
			if (o.nextText) {
				methods._appendItem.call(this, o.currentPage + 1, {text: o.nextText, classes: 'next'});
			}
		},

		_getPages: function(o) {
			var pages = Math.ceil(o.items / o.itemsOnPage);
			return pages || 1;
		},

		_getInterval: function(o) {
			return {
				start: Math.ceil(o.currentPage > o.halfDisplayed ? Math.max(Math.min(o.currentPage - o.halfDisplayed, (o.pages - o.displayedPages)), 0) : 0),
				end: Math.ceil(o.currentPage > o.halfDisplayed ? Math.min(o.currentPage + o.halfDisplayed, o.pages) : Math.min(o.displayedPages, o.pages))
			};
		},

		_appendItem: function(pageIndex, opts) {
			var self = this, options, $link, o = self.data('pagination'), $linkWrapper = $('<li></li>'), $ul = self.find('ul');

			pageIndex = pageIndex < 0 ? 0 : (pageIndex < o.pages ? pageIndex : o.pages - 1);

			options = {
				text: pageIndex + 1,
				classes: ''
			};

			if (o.labelMap.length && o.labelMap[pageIndex]) {
				options.text = o.labelMap[pageIndex];
			}

			options = $.extend(options, opts || {});

			if (pageIndex == o.currentPage || o.disabled) {
				if (o.disabled) {
					$linkWrapper.addClass('disabled');
				} else {
					$linkWrapper.addClass('active');
				}
				$link = $('<span class="current">' + (options.text) + '</span>');
			} else {
				$link = $('<a href="' + o.hrefTextPrefix + (pageIndex + 1) + o.hrefTextSuffix + '" class="page-link">' + (options.text) + '</a>');
				$link.click(function(event){
					return methods._selectPage.call(self, pageIndex, event);
				});
			}

			if (options.classes) {
				$link.addClass(options.classes);
			}

			$linkWrapper.append($link);

			if ($ul.length) {
				$ul.append($linkWrapper);
			} else {
				self.append($linkWrapper);
			}
		},

		_selectPage: function(pageIndex, event) {
			var o = this.data('pagination');
			o.currentPage = pageIndex;
			if (o.selectOnClick) {
				methods._draw.call(this);
			}
			return o.onPageClick(pageIndex + 1, event);
		}

	};

	$.fn.pagination = function(method) {

		// Method calling logic
		if (methods[method] && method.charAt(0) != '_') {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' +  method + ' does not exist on jQuery.pagination');
		}

	};

})(jQuery);
