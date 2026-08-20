<script lang="ts">
  import type { ViewMenuItem, ViewMode } from '$lib/types/view';

  type Props = {
    items?: ViewMenuItem[];
    active: ViewMode;
    onChange: (next: ViewMode) => void;
  };

  let { items = [], active, onChange }: Props = $props();
</script>

<nav aria-label="Näkymän valinta" class="mb-6">
    <ul class="grid gap-3 sm:grid-cols-2">
        {#each items as item}
            <li>
                <button
                        type="button"
                        on:click={() => onChange(item.id)}
                        class={`w-full rounded-xl border p-4 text-left transition ${
						active === item.id
							? 'border-indigo-500 bg-indigo-50 ring-1 ring-indigo-200'
							: 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50'
					}`}
                        aria-pressed={active === item.id}
                >
                    <p class="font-semibold text-slate-900">{item.label}</p>
                    <p class="mt-1 text-sm text-slate-600">{item.description}</p>
                </button>
            </li>
        {/each}
    </ul>
</nav>