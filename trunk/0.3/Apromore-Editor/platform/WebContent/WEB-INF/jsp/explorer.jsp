<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Explorer | Signavio</title>
		
		<!-- Libraries -->
		
		<!--  Prototype -->
		<script type="text/javascript" src="<%= request.getAttribute("libs_url") %>/prototype-1.6.0.3.js"></script>
		
				
		<!--  Ext -->
		<script type="text/javascript" src="<%= request.getAttribute("libs_url") %>/ext-2.0.2/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="<%= request.getAttribute("libs_url") %>/ext-2.0.2/ext-all.js"></script>
		
		<!-- CSS -->
		<link type="text/css" rel="stylesheet" href="<%= request.getAttribute("libs_url") %>/ext-2.0.2/resources/css/ext-all.css" />
		<link type="text/css" rel="stylesheet" href="<%= request.getAttribute("libs_url") %>/ext-2.0.2/resources/css/xtheme-darkgray.css" />
		<link type="text/css" rel="stylesheet" href="<%= request.getAttribute("explorer_url") %>/src/css/xtheme-specific.css" />
		<link type="text/css" rel="stylesheet" href="<%= request.getAttribute("explorer_url") %>/src/css/xtheme-smoky.css" />
		<link type="text/css" rel="stylesheet" href="<%= request.getAttribute("explorer_url") %>/src/css/custom-style.css" />
		
		<!-- Language-Settings -->
		<script type="text/javascript" src="<%= request.getAttribute("explorer_url") %>/data/i18n/translation_en_us.js"></script>
		
		<!-- Libs Utils -->
		<script type="text/javascript" src="<%= request.getAttribute("libs_url") %>/utils.js"></script>
		
		<!-- Explorer -->
		<script type="text/javascript" src="<%= request.getAttribute("explorer_url") %>/explorer.js"></script>
	</head>
	<body>
		<script type="text/javascript">

			Ext.onReady(function(){new Signavio.Core.Repository();});
		
		</script>
	</body>
</html>