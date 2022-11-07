import { Component } from '@angular/core';

@Component({
  selector: 'tb-logo',
  templateUrl: './logo.component.html',
  styleUrls: ['./logo.component.scss']
})
export class LogoComponent {

  logo = 'assets/logo_title_white.svg';

  gotoVizzionnaire(): void {
    window.open('https://vizzionnaire.io', '_blank');
  }

}
