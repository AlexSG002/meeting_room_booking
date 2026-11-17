import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EMPTY } from 'rxjs';
import { ReservationService } from '../../services/reservation-service';
import { ReservationModal } from './reservation-modal';

describe('ReservationModal', () => {
  let component: ReservationModal;
  let fixture: ComponentFixture<ReservationModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationModal],
      providers: [
        {
          provide: ReservationService,
          useValue: {
            getAvailability: () => EMPTY,
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationModal);
    fixture.componentRef.setInput('rooms', []);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
