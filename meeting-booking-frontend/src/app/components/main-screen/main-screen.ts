import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Reservation} from '../../models/reservation.model';
import {ReservationService} from '../../services/reservation-service';

@Component({
  selector: 'app-main-screen',
  imports: [FormsModule],
  templateUrl: './main-screen.html',
  styleUrl: './main-screen.css'
})
export class MainScreen {

  private readonly reservationService = inject(ReservationService);

  readonly hourHeight = 70;
  readonly calendarStartHour = 9;

  selectedRoom = 'Sala Azul';

  selectedDate = new Date(2026, 8, 18);

  reservations: Reservation[] = [];

  rooms = [
    'Sala Azul',
    'Sala Roja'
  ];

  constructor() {
    this.loadReservations();
  }

  getReservationTop(reservation: Reservation): number {
    const startHour = Number(reservation.startTime.slice(11, 13));
    const startMinutes = Number(reservation.startTime.slice(14, 16));

    const totalMinutes =
      (startHour - this.calendarStartHour) * 60 + startMinutes;

    return (totalMinutes / 60) * this.hourHeight;
  }

  getReservationHeight(reservation: Reservation): number {
    const startHour = Number(reservation.startTime.slice(11, 13));
    const startMinutes = Number(reservation.startTime.slice(14, 16));

    const endHour = Number(reservation.endTime.slice(11, 13));
    const endMinutes = Number(reservation.endTime.slice(14, 16));

    const startTotalMinutes = startHour * 60 + startMinutes;
    const endTotalMinutes = endHour * 60 + endMinutes;

    const durationMinutes = endTotalMinutes - startTotalMinutes;

    return (durationMinutes / 60) * this.hourHeight;
  }

  private loadReservations(): void {
    this.reservationService.getAllReservations().subscribe({
      next: (reservations) => {
        this.reservations = reservations;
        console.log('Reservations: ', reservations);
      },
      error: (error) => {
        console.error('Error loading reservatios: ', error)
      }
    })
  }

  get filteredReservations(): Reservation[] {
    const selectedDate = this.formatDate(this.selectedDate);

    return this.reservations.filter(reservation => {
      const reservationDate = reservation.startTime.slice(0,10);

      return reservationDate === selectedDate && reservation.roomName === this.selectedRoom;

    })
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  previousDay(): void {
    this.selectedDate = new Date(
      this.selectedDate.getFullYear(),
      this.selectedDate.getMonth(),
      this.selectedDate.getDate() - 1
    );
  }

  nextDay(): void {
    this.selectedDate = new Date(
      this.selectedDate.getFullYear(),
      this.selectedDate.getMonth(),
      this.selectedDate.getDate() + 1
    );
  }

  createReservation(): void {
    console.log('Create reservation');
  }

  get formattedDate(): string {
    return new Intl.DateTimeFormat('es-ES', {
      weekday: 'long',
      day: 'numeric',
      month: 'long'
    }).format(this.selectedDate);
  }

  get formattedYear(): string {
    return this.selectedDate.getFullYear().toString();
  }
}
