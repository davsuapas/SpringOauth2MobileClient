import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';

import { TokenStore }  from "./token-store";

/*
  Generated class for the GetHelloWorld provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class ServerService {

  constructor(private http: Http, private tokenStore: TokenStore) {
  }

  public getHelloService() {

    this.http.get('http://192.168.1.102:8080/hello', {
      headers: this.GetHeaders()
    }).subscribe(
      r => alert(r.text()),
      e => alert(e.toString())
    );
  }

  public revokeToken() {

    this.http.get('http://192.168.1.102:8080/revokeToken', {
      headers: this.GetHeaders()
    }).subscribe(
      r => alert("Revoked"),
      e => alert(e.toString())
    );
  }

  private GetHeaders(): Headers {
    let authHeader = new Headers();
    authHeader.append('Authorization', 'Bearer ' + this.tokenStore.token);
    return authHeader;
  }
}
