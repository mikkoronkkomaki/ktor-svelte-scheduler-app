export type ViewMode =
  | 'appointment-form'
  | 'specialist-form'
  | 'appointment-search'
  | 'specialist-search';

export interface ViewMenuItem {
  id: ViewMode;
  label: string;
  description: string;
}