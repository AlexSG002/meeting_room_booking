import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MainScreen } from './components/main-screen/main-screen';

@Component({
  imports: [RouterOutlet, MainScreen],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('meeting-booking-frontend');
}
