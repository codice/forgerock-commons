;(function()
{
  // CommonJS
  SyntaxHighlighter = SyntaxHighlighter || (typeof require !== 'undefined'?
      require('shCore').SyntaxHighlighter : null);

  function Brush()
  {
    var keywords = 'add changetype control delete deleteoldrdn dn moddn ' +
                   'modify modrdn newrdn newsuperior replace version';
    
    this.regexList = [
      { regex: new RegExp(this.getKeywords(keywords), 'gmi'),     css: 'keyword' },
      { regex: SyntaxHighlighter.regexLib.singleLinePerlComments, css: 'comments' },
      { regex: SyntaxHighlighter.regexLib.url,                    css: 'a' },
      { regex: /\b(\d+\.)+\d+\b/g,                                css: 'constants' },// OID
      { regex: /\b(true|false)\b/g,                               css: 'constants' },
      { regex: /-(\n|$)/g,                                        css: 'color3' },  // Separator
      { regex: /:<|:&lt;|:/g,                                     css: 'color3' },  // Separator
      { regex: /[\w;-]+(?=:.*(\r|\n)?)/g,                         css: 'string' },  // Attr type
    ];
  };

  Brush.prototype = new SyntaxHighlighter.Highlighter();
  Brush.aliases = ['ldif'];

  SyntaxHighlighter.brushes.LDIF = Brush;

  // CommonJS
  typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
