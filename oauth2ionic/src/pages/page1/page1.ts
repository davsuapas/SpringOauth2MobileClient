import { Component } from '@angular/core';

import { NavController, Platform } from 'ionic-angular';

import { InAppBrowser } from 'ionic-native';

import { TokenStore } from '../../providers/token-store';

@Component({
  selector: 'page-page1',
  templateUrl: 'page1.html'
})
export class Page1 {

  constructor(public navCtrl: NavController, private platform: Platform, private tokenStore: TokenStore) {
  }

  public login() {

    this.platform.ready().then(() => {
      this.oauth2Login().then(success => {
        alert("Token is ok: " + success.access_token)
        this.tokenStore.token = success.access_token;
      }, (error) => {
        alert(error);
      });
    });
  }

  public oauth2Login(): Promise<any> {

      return new Promise(function(resolve, reject) {

        let browserRef = new InAppBrowser("http://192.168.1.102:8080/oauth/authorize?client_id=mobileclient&redirect_uri=http://localhost/callback&response_type=token", "_blank", "location=no,clearsessioncache=yes,clearcache=yes");

        browserRef.on("loadstart").subscribe( event => {

            if ((event.url).indexOf("http://localhost/callback") === 0) {

                browserRef.on("exit").subscribe(event => {});
                browserRef.close();

                var responseParameters = ((event.url).split("#")[1]).split("&");
                var parsedResponse = {};

                for (var i = 0; i < responseParameters.length; i++) {
                    parsedResponse[responseParameters[i].split("=")[0]] = responseParameters[i].split("=")[1];
                }

                if (parsedResponse["access_token"] !== undefined && parsedResponse["access_token"] !== null) {
                    resolve(parsedResponse);
                } else {
                    reject("Problem authenticating with Spring oauth2");
                }
            }
        });

        browserRef.on("exit").subscribe( event => {
            reject("The Spring oauth2 sign in flow was canceled");
        });
      });
  }
}
