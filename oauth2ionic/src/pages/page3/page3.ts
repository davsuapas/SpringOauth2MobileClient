import { Component } from '@angular/core';

import { ServerService } from "../../providers/server-service";

/*
  Generated class for the Page3 page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-page3',
  templateUrl: 'page3.html'
})
export class Page3 {

  constructor(private serverService: ServerService) {
  }

  public revokeToken() {
    this.serverService.revokeToken();
  }
}
