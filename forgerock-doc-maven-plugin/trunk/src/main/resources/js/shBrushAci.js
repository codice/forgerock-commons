;(function()
{
  // CommonJS
  SyntaxHighlighter = SyntaxHighlighter || (typeof require !== 'undefined'?
          require('shCore').SyntaxHighlighter : null);

  function Brush()
  {
    var keywords = 'aci acl add all allow anyone authmethod compare '
        + 'dayofweek  delete deny dns export extop groupdn import ip '
        + 'parent proxy read search self selfwrite ssf target ' +
        + 'targetattr targattrfilters targetcontrol targetfilter '
        + 'targetscope timeofday userattr userdn write';
    /* Need to separate keywords having another keyword as a prefix */
    var repeated = 'allow targetattr targattrfilters targetcontrol '
        + 'targetfilter targetscope';
    this.regexList = [
      { regex: new RegExp(this.getKeywords(keywords), 'gim'), css: 'keyword' },
      { regex: new RegExp(this.getKeywords(repeated), 'gim'), css: 'keyword' },
      { regex: /version 3\.0/g,                               css: 'keyword' },
      { regex: SyntaxHighlighter.regexLib.multiLineDoubleQuotedString, css: 'string' },
      { regex: /[\(\);]/g,                                    css: 'color3' },
    ];
  };

  Brush.prototype = new SyntaxHighlighter.Highlighter();
  Brush.aliases = ['aci'];

  SyntaxHighlighter.brushes.Aci = Brush;

  // CommonJS
  typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();
