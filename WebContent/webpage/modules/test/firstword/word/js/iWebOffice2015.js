var userAgent = navigator.userAgent, 
				rMsie = /(msie\s|trident.*rv:)([\w.]+)/, 
				rFirefox = /(firefox)\/([\w.]+)/, 
				rOpera = /(opera).+version\/([\w.]+)/, 
				rChrome = /(chrome)\/([\w.]+)/, 
				rSafari = /version\/([\w.]+).*(safari)/;
				var browser;
				var version;
				var ua = userAgent.toLowerCase();
				function uaMatch(ua) {
					var match = rMsie.exec(ua);
					if (match != null) {
						return { browser : "IE", version : match[2] || "0" };
					}
					var match = rFirefox.exec(ua);
					if (match != null) {
						return { browser : match[1] || "", version : match[2] || "0" };
					}
					var match = rOpera.exec(ua);
					if (match != null) {
						return { browser : match[1] || "", version : match[2] || "0" };
					}
					var match = rChrome.exec(ua);
					if (match != null) {
						return { browser : match[1] || "", version : match[2] || "0" };
					}
					var match = rSafari.exec(ua);
					if (match != null) {
						return { browser : match[2] || "", version : match[1] || "0" };
					}
					if (match != null) {
						return { browser : "", version : "0" };
					}
				}
				var browserMatch = uaMatch(userAgent.toLowerCase());
				if (browserMatch.browser) {
					browser = browserMatch.browser;
					version = browserMatch.version;
				}

var str = '';

var copyright='金格科技iWebOffice2015智能文档中间件[演示版];V5.0S0xGAAEAAAAAAAAAEAAAAH0BAACAAQAALAAAADN7tq4KLK1xEYSFTPoDHBJ5h/Zl7Kmt5ZVCxb3DFZzRTXi6MzCB4EHNVkKckoP0N0j788U6ID++M7djx1tVid4SUjrZoHkdMTZEfBeNrTzThKKI/XllnHGt6eb7vXmDXanyJkO75vuoZy6njIw9Uy8nS6rKp6wSdG8KLXVJqLIW+9IoLAf9aalFi1bEqxcknVVvt8+cbvm/vbwDkgXyUl2FRj2A/+VRCuuyX8X0m0uNIB6DeTZzVCTwRR23tCcE6UUhbe3Nf/3KdWcCupsBqE07F9glBA0gD75qRhZN+g3GYSkWryq4Qorct5v7XefwnPhD5QCznzkfjkPPO9CpLX+3hAycaWDXG02vpt9Go02ZF//Cn2OEehTgwqtCwRAiDRhQWo21gXwf4OoGu7oMaRxbMx+bHHH5URmATMEEa2amA4RhqDOxr5wr/8+h6R/+SmetoZlF6cnBmy0M9ByepazSQhZ6evPDzOi6lHYBjLlv8kIrxwOY/NOoTn9LQKANmJhwvZznOl17RnIi9Qar+ZU=';

str += '<object id="WebOffice" ';

str += ' width="100%"';
str += ' height="100%"';

if ((window.ActiveXObject!=undefined) || (window.ActiveXObject!=null) ||"ActiveXObject" in window)
{
	if(window.navigator.platform == "Win32")
		str += ' CLASSID="CLSID:D89F482C-5045-4DB5-8C53-D2C9EE71D025"  codebase="iWebOffice2015.cab#version=12,4,0,442"';
	if (window.navigator.platform == "Win64")
		str += ' CLASSID="CLSID:D89F482C-5045-4DB5-8C53-D2C9EE71D024"  codebase="iWebOffice2015.cab#version=12,4,0,442"';
	str += '>';
	str += '<param name="Copyright" value="' + copyright + '">';
}
else if (browser == "chrome") 
{
	str += ' clsid="CLSID:D89F482C-5045-4DB5-8C53-D2C9EE71D025"';        
	str += ' type="application/kg-plugin"';
	str += ' OnCommand="OnCommand"';
	str += ' OnRightClickedWhenAnnotate="OnRightClickedWhenAnnotate"';
	str += ' OnSending="OnSending"';
	str += ' OnSendEnd="OnSendEnd"';
	str += ' OnRecvStart="OnRecvStart"';
	str += ' OnRecving="OnRecving"';
	str += ' OnRecvEnd="OnRecvEnd"';
	str += ' OnFullSizeBefore="OnFullSizeBefore"';
	str += ' OnFullSizeAfter="OnFullSizeAfter"';	
	str += ' Copyright="' + copyright + '"';
}
else if (browser == "firefox") 
{
	str += ' clsid="CLSID:D89F482C-5045-4DB5-8C53-D2C9EE71D025"';
	str += ' type="application/kg-activex"';
	str += ' OnCommand="OnCommand"';
	str += ' OnReady="OnReady"';
	str += ' OnOLECommand="OnOLECommand"';
	str += ' OnExecuteScripted="OnExecuteScripted"';
	str += ' OnQuit="OnQuit"';
	str += ' OnSendStart="OnSendStart"';
	str += ' OnSending="OnSending"';
	str += ' OnSendEnd="OnSendEnd"';
	str += ' OnRecvStart="OnRecvStart"';
	str += ' OnRecving="OnRecving"';
	str += ' OnRecvEnd="OnRecvEnd"';
	str += ' OnRightClickedWhenAnnotate="OnRightClickedWhenAnnotate"';
	str += ' OnFullSizeBefore="OnFullSizeBefore"';
	str += ' OnFullSizeAfter="OnFullSizeAfter"';	
	str += ' Copyright="' + copyright + '"';
	str += '>';
}
str += '</object>';
document.write(str); 
//alert(str);
//谷歌中加载插件
function OnControlCreated()
{
	if (browser == "chrome") 
	{
		KGChromePlugin = document.getElementById('WebOffice');
		iWebOffice = KGChromePlugin.obj;
		WebOfficeObj.setObj(iWebOffice);
		Load();
	}
}