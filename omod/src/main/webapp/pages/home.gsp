<% ui.setPageTitle(ui.message("uiframework.home.title")) %>

${ ui.includeFragment("uiframework", "helloUser", [ user: authenticatedUser ]) }
