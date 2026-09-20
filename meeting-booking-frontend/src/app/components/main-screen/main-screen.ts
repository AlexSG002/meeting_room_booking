import {Component, inject, ChangeDetectorRef} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Reservation} from '../../models/reservation.model';
import {ReservationService} from '../../services/reservation-service';
import { ReservationModal } from '../reservation-modal/reservation-modal';
import { Room } from '../../models/room.model';
import { RoomService } from '../../services/room-service';

@Component({
  selector: 'app-main-screen',
  imports: [FormsModule, ReservationModal],
  templateUrl: './main-screen.html',
  styleUrl: './main-screen.css'
})
export class MainScreen {

  private readonly reservationService = inject(ReservationService);
  private readonly roomService = inject(RoomService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly hourHeight = 70;
  readonly calendarStartHour = 9;

  showReservationModal = false;

  selectedRoom = '';

  selectedDate = new Date();

  reservations: Reservation[] = [];

  rooms: Room[] = [];
  constructor() {
    this.loadReservations();
    this.loadRooms();
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
        this.cdr.detectChanges();
        console.log('Reservations: ', reservations);
      },
      error: (error) => {
        console.error('Error loading reservatios: ', error)
      }
    })
  }

  private loadRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms;

        if(rooms.length > 0){
          this.selectedRoom = rooms[0].name;
        }
        this.cdr.detectChanges();
        console.log('Rooms:', rooms);
      },
      error: (error) => {
        console.error('Error loading rooms: ')
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
    this.showReservationModal = true;
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
