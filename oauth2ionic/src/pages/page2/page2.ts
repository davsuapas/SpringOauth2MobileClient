import { Component } from '@angular/core';

import { ServerService } from "../../providers/server-service";

@Component({
  selector: 'page-page2',
  templateUrl: 'page2.html'
})
export class Page2 {

  constructor(private serverService: ServerService) {
  }

  public getHelloWorld() {
    this.serverService.getHelloService();
  }
}
