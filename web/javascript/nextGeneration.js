/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


// TODO clear  $('#tabs-byProfile')
// get weights input by user for artifacts
// post weights to newGenRequest servlet as key value pairs (name of html file, score given by user)
// then return results by generating the table
/*
 *  xhr.onreadystatechange = function ()
        {
            if (xhr.readyState === 4)
            {
                var result = JSON.parse(xhr.responseText);
              
                var content = "<table><tr>";
                for (var i = 0; i < result.length; i++) {
                    if(i % 3 === 0){content += "<tr>";}
                 content += "<td><iframe src='" + result[i] + "'></iframe></td>";  //scrolling='no' class='tableframes'
                      if(i % 3 === 2){content += "</tr>";} 
                }
                content += "</tr></table>";
               $('#tabs-byProfile').append(content);
          }
        };
 */