import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';


/**
* Update all controls of the provided form group with the given data.
*/
export function updateForm(group: FormGroup, data: any) {
  for (const field in group.controls) {
    const control = group.get(field)!;
    let value = data[field] === undefined ? null : data[field];
    control.setValue(value);
  }
}

/**
 * Get the current parameters relevant for a list request.
 */
export function getListParams(route: ActivatedRoute) {
    let params:Record<string,string> = {};
    const page = route.snapshot.queryParamMap.get('page');
    if (page) {
      params['page'] = page;
    }
    const size = route.snapshot.queryParamMap.get('size');
    if (size) {
      params['size'] = size;
    }
    const sort = route.snapshot.queryParamMap.get('sort');
    if (sort) {
      params['sort'] = sort;
    }
    const filter = route.snapshot.queryParamMap.get('filter');
    if (filter) {
      params['filter'] = filter;
    }
    return params;
}

/**
 * Helper function for transforming a Record to a Map to support number as a key.
 */
export function transformRecordToMap(data:Record<number,number|string>):Map<number,string> {
  const dataMap = new Map();
  for (const [key, value] of Object.entries(data)) {
    dataMap.set(+key, '' + value);
  }
  return dataMap;
}
