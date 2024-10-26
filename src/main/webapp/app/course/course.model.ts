export class CourseDTO {

  constructor(data:Partial<CourseDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  name?: string|null;
  code?: string|null;
  credits?: number|null;

}
