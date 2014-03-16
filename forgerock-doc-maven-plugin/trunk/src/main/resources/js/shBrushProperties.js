;(function()
{
  // CommonJS
  typeof(require) != 'undefined' ? SyntaxHighlighter =
    require('shCore').SyntaxHighlighter : null;

  function Brush()
  {
    this.regexList = [
      { regex: /[#!;].+(\n|\r)/g,                        css: 'comments'},
      { regex: SyntaxHighlighter.regexLib.url,           css: 'a'},
      { regex: /[=:]/g,                                  css: 'color3'},
      { regex: /\[\w+\]/g,                               css: 'keyword'},
      { regex: /([^\s]|\\ )+(\s+)?(?=(=|:).+(\n|\r)?)/g, css: 'string'},
    ];
  };

  Brush.prototype = new SyntaxHighlighter.Highlighter();
  Brush.aliases = ['ini', 'properties'];

  SyntaxHighlighter.brushes.Properties = Brush;

  // CommonJS
  typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
