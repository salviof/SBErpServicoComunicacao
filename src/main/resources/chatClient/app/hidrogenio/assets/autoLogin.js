


function waitForElementToExist(selector) {
    return new Promise(resolve => {
      if (document.querySelector(selector)) {
        return resolve(document.querySelector(selector));
      }
  
      const observer = new MutationObserver(() => {
        if (document.querySelector(selector)) {
          resolve(document.querySelector(selector));
          observer.disconnect();
        }
      });
  
      observer.observe(document.body, {
        subtree: true,
        childList: true,
      });
    });
  }
  
  function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
  }  
  

function timeout(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
async function sleep(fn, ...args) {
    await timeout(5000);
    return fn(...args);
}

  const  monitorLogin =  async ()=>{
    
    window.infoLoginJSESSIONID= getCookie("JSESSIONID_CRM");
    if (infoLoginJSESSIONID == undefined){
       alert("sessão não definida"); 
       return;
    }
    window.infoLoginCRM_HOST=getCookie("URL_CRM");
    if (infoLoginCRM_HOST == undefined){
      alert("urlCRM não definido"); 
      return;
   }
   
    window.infoLoginROOM_ID=getCookie("ROOM_ID");
    if (infoLoginJSESSIONID == undefined){
      alert("room ID não definido"); 
    }
    
    
   
    sessaoPickerModelLoaded(10000000).then(function(){
        if (window.__hydrogenViewModel.sessionPickerViewModel.sessions.length>0){
            window.location.href = '/hidrogenio/index.html#/session'; 
        }else {
            window.location.href = '/hidrogenio/index.html#/login'; 
        }
    });
    waitForElementToExist('#username').then(element => {

        loginModelLoaded(10000000).then(function(){
           let urlDadosAcesso=  '/api/v1/chatClient/dadosSessao.json'
           console.log("url dados");
           console.log(urlDadosAcesso);
          fetch(urlDadosAcesso).then(res => res.json())
          .then(json => {
            if (json.autenticado){
                window.__hydrogenViewModel.loginViewModel.passwordLoginViewModel.login(json.usuario,json.senha);                
            }
          }).catch(err => { 
            alert("erro acessando dados do usuário");
            console.log(err);
          });
           
        });
      });    
         
      

      waitForElementToExist('.room-placeholder').then(element => {
          
            window.location.href = '/hidrogenio/index.html#/session/'+window.__hydrogenViewModel.sessionViewModel.id+'/room/'+getCookie('ROOM_ID'); 
        
      });       


      waitForElementToExist('.SessionPickerView').then(element => {
        sessaoPickerModelLoaded(10000000).then(function(){
            if (window.__hydrogenViewModel.sessionPickerViewModel.sessions.length>0){
                window.location.href = '/hidrogenio/index.html#/session/'+window.__hydrogenViewModel.sessionPickerViewModel.sessions.get(0).id+"/room/"+getCookie('ROOM_ID');    
            }else {
                window.location.href = '/hidrogenio/index.html#/login'; 
            }
        });
      });       
      

  }

  

  window.addEventListener('load', function () {
    monitorLogin();      
    
        
    
})     


 

 
// This is the promise code, so this is the useful bit
function loginModelLoaded(timeout) {
    var start = Date.now();
    return new Promise(waitForFoo); // set the promise object within the ensureFooIsSet object
 
    // waitForFoo makes the decision whether the condition is met
    // or not met or the timeout has been exceeded which means
    // this promise will be rejected
    function waitForFoo(resolve, reject) {
        if (window.__hydrogenViewModel && window.__hydrogenViewModel.loginViewModel)
            resolve(window.__hydrogenViewModel.loginViewModel);
        else if (timeout && (Date.now() - start) >= timeout)
            reject(new Error("timeout"));
        else
            setTimeout(waitForFoo.bind(this, resolve, reject), 1000);
    }
}

// This is the promise code, so this is the useful bit
function sessaoPickerModelLoaded(timeout) {
    var start = Date.now();
    return new Promise(waitForFoo); // set the promise object within the ensureFooIsSet object
 
    // waitForFoo makes the decision whether the condition is met
    // or not met or the timeout has been exceeded which means
    // this promise will be rejected
    function waitForFoo(resolve, reject) {
        if (window.__hydrogenViewModel != undefined && window.__hydrogenViewModel.sessionPickerViewModel!=undefined)
            resolve(window.__hydrogenViewModel.sessionPickerViewModel);
        else if (timeout && (Date.now() - start) >= timeout)
            reject(new Error("timeout"));
        else
            setTimeout(waitForFoo.bind(this, resolve, reject), 1000);
    }
}
 
  