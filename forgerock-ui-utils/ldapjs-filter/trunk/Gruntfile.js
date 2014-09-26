module.exports = function(grunt) {

    grunt.initConfig({
        svn: {},
        requirejs: {
            compile: {
                options: {
                    baseUrl : "src",
                    include: [ "../almond", "ldapjs-filter" ],
                    out: "target/ldapjs-filter-<%= svninfo.rev %>.min.js",
                    wrap: {
                        "startFile": "wrap.start",
                        "endFile": "wrap.end"
                    }
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-svn');

    grunt.loadNpmTasks('grunt-contrib-requirejs');
    

    
    grunt.registerTask('default', ['svninfo','requirejs']);
};
