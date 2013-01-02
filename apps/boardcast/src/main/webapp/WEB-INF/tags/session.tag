<c:if test="${not empty sessionScope.username}">
    <script type="text/javascript">
        //we're logged into the server but not the client, so we remove the anonymous
        //login from the client this means that the client code will now use
        //the non-anonymous server login.
        if (window.sessionStorage.getItem('boardcast.identity').indexOf('anon,') === 0) {
            window.sessionStorage.setItem('boardcast.identity', '');
        }
    </script>
</c:if>
