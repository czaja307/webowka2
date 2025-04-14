<script setup lang="ts">
import {computed, ref} from 'vue'
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import {Button} from "@/components/ui/button";
import {ArrowLeftIcon, ArrowRightIcon} from "@radix-icons/vue";
import {Input} from "@/components/ui/input";
import {Card, CardContent} from "@/components/ui/card";
import {Checkbox} from "@/components/ui/checkbox";
import {Dialog, DialogClose, DialogContent, DialogHeader, DialogTitle, DialogTrigger,} from '@/components/ui/dialog'


interface Author {
  id: string
  firstName: string
  lastName: string
  birthDate: string | Date
}


interface AuthorResponse {
  content: Author[]
  totalPages: number
}


const queryClient = useQueryClient();

const page = ref(0);
const totalPages = ref(1);
const itemsPerPage = ref(3);
const editingId = ref(null);
const currentAuthor = ref<Author>({
  id: '',
  firstName: 'string',
  lastName: 'string',
  birthDate: 'string'
});

const queryKey = computed(() => {
  return ['authors', page.value, itemsPerPage.value];
});

const fetchAuthors = async ({queryKey}: {
  queryKey: (string | number)[]
}) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/authors?page=${page.value}&size=${itemsPerPage.value}`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as BookResponse;
  totalPages.value = data.totalPages;
  console.log(totalPages.value)
  return data;
};

const {data, error, isLoading, isError} = useQuery({
  queryKey: queryKey.value,
  queryFn: fetchAuthors,
  keepPreviousData: true,
})


const authors = computed(() => data.value ? data.value.content : [])

const deleteMutation = useMutation({
  mutationFn: async (id: string) => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/authors/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      const errorResponse = await response.text();
      console.log(errorResponse)
      throw new Error(errorResponse || 'Network response was not ok');
    }
    return id;
  },
  onSuccess: (id) => {
    queryClient.invalidateQueries({queryKey: ['authors']});
  },
  onError: (error: Error) => {
    alert(error.message);
  },
});

const deleteBook = async (id: string) => {
  await deleteMutation.mutateAsync(id);
};

const updateMutation = useMutation({
  mutationFn: async ({method, id, updatedData}: {
    method: "PUT" | "POST",
    id: string | null,
    updatedData: Partial<Book>
  }) => {
    let endpoint = `${import.meta.env.VITE_API_URL}/authors/${id}`;
    if (method === "POST") {
      console.log("kkkk")
      endpoint = `${import.meta.env.VITE_API_URL}/authors`;
    }
    const response = await fetch(endpoint, {
      method: method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(updatedData),
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json() as Promise<Book>;
  },
  onSuccess: () => {
    queryClient.invalidateQueries({queryKey: ['authors']});
  },
});

const nextPage = () => {
  if (page.value < totalPages.value - 1) {
    page.value++;
  }
  queryClient.invalidateQueries({queryKey: ['authors']});
};

const prevPage = () => {
  if (page.value > 0) {
    page.value--;
  }
  queryClient.invalidateQueries({queryKey: ['authors']});
};

const startEditing = (author: Book) => {
  editingId.value = author.id;
  currentAuthor.value = {
    id: author.id,
    firstName: author.firstName,
    lastName: author.lastName,
    birthDate: author.birthDate,
  };
};

const saveAuthor = async (isBeingAdded?: boolean) => {
  await updateMutation.mutateAsync(
      {
        method: isBeingAdded ? "POST" : "PUT",
        id: editingId.value,
        updatedData: {
          firstName: currentAuthor.value.firstName,
          lastName: currentAuthor.value.lastName,
          birthDate: currentAuthor.value.birthDate,
        }
      });
  editingId.value = null;
  currentAuthor.value = {
    id: '',
    firstName: 'string',
    lastName: 'string',
    birthDate: 'string'
  };
};


const cancelEditing = () => {
  editingId.value = null;
  currentAuthor.value = {
    id: '',
    firstName: 'string',
    lastName: 'string',
    birthDate: 'string'
  };
};

</script>


<template>
  <div class="flex items-center h-screen flex-col gap-10 p-4">
    <div class="flex flex-row w-full justify-between">
      <h1 class="font-bold text-xl">Authors</h1>
      <Dialog>
        <DialogTrigger as-child>
          <Button>Add</Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add a new author</DialogTitle>
          </DialogHeader>
          <Card class="sm:p-4 p-1">
            <CardContent class="flex flex-col gap-4">
              <div class="justify-start flex flex-col">
                <label for="name" class="text-sm text-gray-600">First Name</label>
                <Input
                    v-model="currentAuthor.firstName"
                    type="text"
                    id="name"
                />
              </div>
              <div class="justify-start flex flex-col">
                <label for="lname" class="text-sm text-gray-600">Last Name</label>
                <Input
                    v-model="currentAuthor.lastName"
                    type="text"
                    id="lname"
                />
              </div>
              <div class="justify-start flex flex-col">
                <label for="Birthdate" class="text-sm text-gray-600">Birthdate</label>
                <Input
                    v-model="currentAuthor.birthDate"
                    type="date"
                    id="Birthdate"
                />
              </div>
              <div class="justify-start flex flex-col">
                <Button
                    class="mt-2"
                    @click="saveAuthor(true)"
                >
                  Save
                </Button>
                <DialogClose as-child>
                  <Button
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                  >
                    Cancel
                  </Button>
                </DialogClose>
              </div>
            </CardContent>
          </Card>
        </DialogContent>
      </Dialog>
    </div>

    <div v-if="isLoading">
      Loading...
    </div>

    <div v-else-if="isError">
      Error: {{ error.message }}
    </div>

    <div v-else>
      <ul>
        <li v-for="author in authors" :key="author.id">
          <div class="p-10">
            <Card class="sm:p-4 p-1">
              <CardContent class="flex flex-col gap-4">
                <div class="justify-start flex flex-col">
                  <label for="Name" class="text-sm text-gray-600">Name</label>
                  <Input
                      v-if="editingId !== author.id"
                      v-model="author.firstName"
                      type="text"
                      id="Name"
                      :disabled="editingId !== author.id"
                  />
                  <Input
                      v-if="editingId === author.id"
                      v-model="currentAuthor.firstName"
                      type="text"
                      id="Name"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="lName" class="text-sm text-gray-600">Last Name</label>
                  <Input
                      v-if="editingId !== author.id"
                      v-model="author.lastName"
                      type="text"
                      id="lName"
                      :disabled="editingId !== author.id"
                  />
                  <Input
                      v-if="editingId === author.id"
                      v-model="currentAuthor.lastName"
                      type="text"
                      id="lName"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="pages" class="text-sm text-gray-600">Birthdate</label>
                  <Input
                      v-if="editingId !== author.id"
                      v-model="author.birthDate"
                      type="date"
                      id="pages"
                      :disabled="editingId !== author.id"
                  />
                  <Input
                      v-if="editingId === author.id"
                      v-model="currentAuthor.birthDate"
                      type="date"
                      id="pages"
                      :disabled="editingId !== author.id"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <Button
                      v-if="editingId !== author.id"
                      class="mt-2"
                      @click="startEditing(author)"
                  >
                    Edit
                  </Button>
                  <Button
                      v-if="editingId !== author.id"
                      class="mt-2"
                      @click="deleteBook(author.id)"
                      variant="destructive"
                  >Delete
                  </Button>
                  <Button
                      v-if="editingId === author.id"
                      class="mt-2"
                      @click="saveAuthor(false)"
                  >
                    Save
                  </Button>
                  <Button
                      v-if="editingId === author.id"
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                  >
                    Cancel
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </li>
      </ul>

    </div>

    <div class="flex flex-row gap-4 items-center p-4">
      <Button variant="outline" @click="prevPage">
        <ArrowLeftIcon/>
      </Button>
      <p>
        {{ page + 1 }}
      </p>
      <Button variant="outline" @click="nextPage">
        <ArrowRightIcon/>
      </Button>
    </div>


  </div>

</template>

<style scoped>

</style>