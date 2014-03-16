;(function()
{
  // CommonJS
  typeof(require) != 'undefined' ? SyntaxHighlighter =
    require('shCore').SyntaxHighlighter : null;

  function Brush()
  {
    this.regexList = [
      { regex: /[#!;].+(\n|\r)/g,                       css: 'comments'},
      { regex: SyntaxHighlighter.regexLib.url,          css: 'a'},
      { regex: /,/g,                                    css: 'color3'},
      { regex: /[^,"';]/g,                              css: 'string'},
    ];
  };

  Brush.prototype = new SyntaxHighlighter.Highlighter();
  Brush.aliases = ['csv'];

  SyntaxHighlighter.brushes.Csv = Brush;

  // CommonJS
  typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
