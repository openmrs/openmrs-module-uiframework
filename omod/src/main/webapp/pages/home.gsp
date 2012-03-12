<% ui.setPageTitle(ui.message("uiframework.home.title")) %>

${ ui.includeFragment("helloUser", [ user: authenticatedUser ]) }
