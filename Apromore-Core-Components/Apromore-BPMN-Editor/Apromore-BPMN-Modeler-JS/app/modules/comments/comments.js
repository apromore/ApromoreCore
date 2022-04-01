import $ from 'jquery';

import {
  getComments,
  removeComment,
  addComment
} from './util';


export default function Comments(config, eventBus, overlays, bpmnjs) {

  function toggleCollapse(element) {

    var o = overlays.get({ element: element, type: 'comments' })[0];

    var $overlay = o && o.html;

    if ($overlay) {

      var expanded = $overlay.is('.expanded');

      eventBus.fire('comments.toggle', { element: element, active: !expanded });

      if (expanded) {
        $overlay.removeClass('expanded');
      } else {
        $overlay.addClass('expanded');
        $overlay.find('textarea').focus();
      }
    }
  }

  function createCommentBox(element) {

    var $overlay = $(Comments.OVERLAY_HTML);

    $overlay.find('.toggle').click(function(e) {
      toggleCollapse(element);
    });

    var $commentCount = $overlay.find('[data-comment-count]'),
        $textarea = $overlay.find('textarea'),
        $comments = $overlay.find('.comments');


    function renderComments() {

      // clear innerHTML
      $comments.html('');

      var comments = getComments(element);

      comments.forEach(function(val) {
        var $comment = $(Comments.COMMENT_HTML);

        $comment.find('[data-text]').text(val[1] + ((val[0]) ? (' (' + val[0] + ')') : ''));
        $comment.find('[data-delete]').click(function(e) {

          e.preventDefault();

          removeComment(element, val);
          renderComments();
          $textarea.val(val[1]);
        });

        $comments.append($comment);
      });

      $overlay[comments.length ? 'addClass' : 'removeClass']('with-comments');

      $commentCount.text(comments.length ? ('(' + comments.length + ')') : '');

      eventBus.fire('comments.updated', { comments: comments });
    }

    $textarea.on('keydown', function(e) {
      if (e.which === 13 && !e.shiftKey) {
        e.preventDefault();

        var comment = $textarea.val();
        // console.log('config.username', config.username);
        if (comment) {
          addComment(element, config.username || '', comment);
          $textarea.val('');
          renderComments();
        }
      }
    });


    // attach an overlay to a node
    overlays.add(element, 'comments', {
      position: {
        bottom: 10,
        right: 10
      },
      html: $overlay
    });

    renderComments();
  }

  eventBus.on('shape.added', function(event) {
    var element = event.element;

    if (element.labelTarget ||
       !element.businessObject.$instanceOf('bpmn:FlowNode')) {

      return;
    }

    defer(function() {
      createCommentBox(element);
    });

  });

  this.collapseAll = function() {

    overlays.get({ type: 'comments' }).forEach(function(c) {
      var html = c.html;
      if (html.is('.expanded')) {
        toggleCollapse(c.element);
      }
    });

  };
}

Comments.$inject = [ 'config', 'eventBus', 'overlays', 'bpmnjs' ];

Comments.OVERLAY_HTML =
  '<div class="comments-overlay">' +
    '<div class="toggle">' +
      '<span class="comments-icon-comment"></span>' +
      '<span class="comment-count" data-comment-count></span>' +
    '</div>' +
    '<div class="content">' +
      '<div class="comments"></div>' +
      '<div class="edit">' +
        '<textarea tabindex="1" placeholder="Type a comment and press Return"></textarea>' +
      '</div>' +
    '</div>' +
  '</div>';

Comments.COMMENT_HTML =
  '<div class="comment">' +
    '<div data-text></div><a href class="delete comments-icon-delete" data-delete></a>' +
  '</div>';


// helpers ///////////////

function defer(fn) {
  setTimeout(fn, 0);
}
