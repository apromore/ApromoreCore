// var translations = require('./translations');
var translationsEn = require('./translations-en');
var translationsJa = require('./translations-ja');
var translationMap = {
  'en': translationsEn,
  'ja': translationsJa,
};
var currentLangTag = 'en';

module.exports = function(langTag) {
  currentLangTag = langTag;
  return function(template, replacements) {
    replacements = replacements || {};

    // Translate
    template = (translationMap[currentLangTag] && translationMap[currentLangTag][template]) ||
      translationsEn[template] ||
      template;

    // Replace
    return template.replace(/{([^}]+)}/g, function(_, key) {
      return replacements[key] || '{' + key + '}';
    });
  };
};