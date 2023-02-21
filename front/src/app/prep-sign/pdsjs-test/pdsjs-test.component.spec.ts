import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PdsjsTestComponent } from './pdsjs-test.component';

describe('PdsjsTestComponent', () => {
  let component: PdsjsTestComponent;
  let fixture: ComponentFixture<PdsjsTestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PdsjsTestComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PdsjsTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
